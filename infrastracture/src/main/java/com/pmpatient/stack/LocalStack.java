package com.pmpatient.stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Token;
import software.amazon.awscdk.services.ec2.ISubnet;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.AwsLogDriverProps;
import software.amazon.awscdk.services.ecs.CloudMapNamespaceOptions;
import software.amazon.awscdk.services.ecs.CloudMapOptions;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ContainerDefinitionOptions;
import software.amazon.awscdk.services.ecs.ContainerImage;
import software.amazon.awscdk.services.ecs.FargateService;
import software.amazon.awscdk.services.ecs.FargateTaskDefinition;
import software.amazon.awscdk.services.ecs.LogDriver;
import software.amazon.awscdk.services.ecs.PortMapping;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.elasticache.CfnCacheCluster;
import software.amazon.awscdk.services.elasticache.CfnSubnetGroup;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.Credentials;
import software.amazon.awscdk.services.rds.DatabaseInstance;
import software.amazon.awscdk.services.rds.DatabaseInstanceEngine;
import software.amazon.awscdk.services.rds.PostgresEngineVersion;
import software.amazon.awscdk.services.rds.PostgresInstanceEngineProps;
import software.amazon.awscdk.services.route53.CfnHealthCheck;
import software.amazon.awscdk.services.servicediscovery.DnsRecordType;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalStack extends Stack {

    private static final String LOCAL_STACK_NAME = "localstack";

    private final Vpc vpc;
    private final Cluster ecsCluster;
    private final SecretsManagerClient secretsManagerClient;
    private final CfnCacheCluster elasticCacheCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);
        this.vpc = createVpc();
        this.secretsManagerClient = createSecretManagerClient();

        DatabaseInstance authServiceDB =
                createDatabase("AuthServiceDB", "auth-service-db");
        DatabaseInstance patientServiceDB =
                createDatabase("PatientServiceDB", "patient-service-db");
        CfnHealthCheck authDbHealthCheck =
                createDbHealthCheck(authServiceDB, "AuthServiceDBHealthCheck");
        CfnHealthCheck patientDbHealthCheck =
                createDbHealthCheck(patientServiceDB, "PatientServiceDBHealthCheck");

        CfnCluster mskCluster = createMskCluster();
        this.ecsCluster = createEcsCluster();
        this.elasticCacheCluster = createRedisCluster();

        FargateService authService = createFargateService("AuthService",
                "auth-service",
                List.of(8079),
                authServiceDB,
                Map.of("JWT_SECRET", getSecretOfName("jwtSecret")));
        authService.getNode().addDependency(authDbHealthCheck);
        authService.getNode().addDependency(authServiceDB);

        FargateService billingService = createFargateService("BillingService",
                "billing-service",
                List.of(8081, 9091),
                null,
                null);

        FargateService analyticsService = createFargateService("AnalyticsService",
                "analytics-service",
                List.of(8082),
                null,
                null);
        analyticsService.getNode().addDependency(mskCluster);

        FargateService patientService = createFargateService("PatientService",
                "patient-service",
                List.of(8080),
                patientServiceDB,
                Map.of( // change use from docker implicit dns networking to ECS cloud discovery functionality with patient-management.local namespace
                        "BILLING_SERVICE_ADDRESS", "billing-service.patient-management.local",
                        "BILLING_SERVICE_GRPC_PORT", "9091"
                ));
        patientService.getNode().addDependency(patientServiceDB);
        patientService.getNode().addDependency(patientDbHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);
        patientService.getNode().addDependency(elasticCacheCluster);

        ApplicationLoadBalancedFargateService apiGateway = createApiGatewayService();
        apiGateway.getNode().addDependency(elasticCacheCluster);

        FargateService prometheusService = createFargateService(
                "PrometheusService",
                "prometheus-localstack",
                List.of(9090),
                null,
                null
        );
        createGrafanaService();
    }

    private Vpc createVpc() {
        return Vpc.Builder.create(this, "PatientManagementVPC")
                .vpcName("PatientManagementVPC")
                .maxAzs(2)
                .build();
    }

    private DatabaseInstance createDatabase(String id, String dbName) {
        return DatabaseInstance.Builder.create(this, id)
                .engine(DatabaseInstanceEngine.postgres(
                        PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("postgres"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                        .ipAddress(db.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }

    private CfnCluster createMskCluster() {
        return CfnCluster.Builder.create(this, "MskCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("3.8.0")
                .numberOfBrokerNodes(vpc.getPrivateSubnets().size())
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets()
                                .stream()
                                .map(ISubnet::getSubnetId)
                                .collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT")
                        .build()
                ).build();
    }

    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this, "PatientManagementCluster")
                .vpc(vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.local")
                        .build())
                .build();
    }

    private FargateService createFargateService(String id,
                                                String imageName,
                                                List<Integer> ports,
                                                DatabaseInstance db,
                                                Map<String, String> additionalEnvVars) {
        FargateTaskDefinition taskDefinition =
                FargateTaskDefinition.Builder.create(this, id + "Task")
                        .cpu(256)
                        .memoryLimitMiB(512)
                        .build();
        ContainerDefinitionOptions.Builder containerDefinitionOptions =
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromRegistry(imageName)) // on prod ECR registry
                        .portMappings(ports.stream()
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList())
                        .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                        .logGroupName("/ecs/" + imageName)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix(imageName)
                                .build()));

        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS",
                "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

        envVars.put("SPRING_CACHE_TYPE", "redis");
        envVars.put("SPRING_DATA_REDIS_HOST", elasticCacheCluster.getAttrRedisEndpointAddress());
        envVars.put("SPRING_DATA_REDIS_PORT", elasticCacheCluster.getAttrRedisEndpointPort());

        envVars.put("SPRING_PROFILES_ACTIVE", "prod");
        if (additionalEnvVars != null) envVars.putAll(additionalEnvVars);
        if (db != null) {
            envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(
                    db.getDbInstanceEndpointAddress(),
                    db.getDbInstanceEndpointPort(),
                    imageName));
            envVars.put("SPRING_DATASOURCE_USERNAME", "postgres");
            envVars.put("SPRING_DATASOURCE_PASSWORD",
                    db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }

        containerDefinitionOptions.environment(envVars);
        taskDefinition.addContainer(imageName + "Container", containerDefinitionOptions.build());
        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(taskDefinition)
                .assignPublicIp(false)
                .cloudMapOptions(CloudMapOptions.builder()
                        .name(imageName)
                        .dnsRecordType(DnsRecordType.A)
                        .build())
                .serviceName(imageName)
                .build();
    }

    private ApplicationLoadBalancedFargateService createApiGatewayService() {
        FargateTaskDefinition taskDefinition =
                FargateTaskDefinition.Builder.create(this, "APIGatewayTaskDefinition")
                        .cpu(256)
                        .memoryLimitMiB(512)
                        .build();
        ContainerDefinitionOptions containerDefinitionOptions =
                ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromRegistry("api-gateway")) // on prod ECR registry
                        .environment(Map.of( // now: ECS cloud discovery, before: docker dns networking: "http://host.docker.internal:8079"
                                "SPRING_PROFILES_ACTIVE", "localstack",
                                "AUTH_SERVICE_URL", "http://auth-service.patient-management.local:8079",
                                "SPRING_DATA_REDIS_HOST", elasticCacheCluster.getAttrRedisEndpointAddress(),
                                "SPRING_DATA_REDIS_PORT", elasticCacheCluster.getAttrRedisEndpointPort()
                        ))
                        .portMappings(Stream.of(7950)
                                .map(port -> PortMapping.builder()
                                        .containerPort(port)
                                        .hostPort(port)
                                        .protocol(Protocol.TCP)
                                        .build())
                                .toList())
                        .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, "ApiGatewayLogGroup")
                                        .logGroupName("/ecs/api-gateway")
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix("api-gateway")
                                .build()))
                        .build();
        taskDefinition.addContainer("APIGatewayContainer", containerDefinitionOptions);
        return ApplicationLoadBalancedFargateService.Builder.create(this, "APIGatewayService")
                        .cluster(ecsCluster)
                        .serviceName("api-gateway")
                        .taskDefinition(taskDefinition)
                        .desiredCount(1)
                        .healthCheckGracePeriod(Duration.seconds(60))
                        .publicLoadBalancer(true)
                        .cloudMapOptions(CloudMapOptions.builder()
                                .name("api-gateway")
                                .dnsRecordType(DnsRecordType.A)
                                .build())
                        .build();
    }

    private CfnCacheCluster createRedisCluster() {
        CfnSubnetGroup redisSubnetGroup = CfnSubnetGroup.Builder
                .create(this, "RedisSubnetGroup")
                .description("Redis/elasticache subnet group")
                .subnetIds(vpc.getPrivateSubnets().stream()
                        .map(ISubnet::getSubnetId)
                        .collect(Collectors.toList()))
                .build();
        return CfnCacheCluster.Builder
                .create(this, "RedisCluster")
                .cacheNodeType("cache.t2.micro")
                .engine("redis")
                .numCacheNodes(vpc.getPrivateSubnets().size())
                .cacheSubnetGroupName(redisSubnetGroup.getCacheSubnetGroupName())
                .vpcSecurityGroupIds(List.of(vpc.getVpcDefaultSecurityGroup()))
                .build();
    }

    private ApplicationLoadBalancedFargateService createGrafanaService() {
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder
                .create(this, "GrafanaTaskDefinition")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();
        taskDefinition.addContainer("GrafanaContainer", ContainerDefinitionOptions.builder()
                        .image(ContainerImage.fromRegistry("grafana/grafana"))
                        .portMappings(List.of(PortMapping.builder()
                                        .containerPort(3000)
                                .build()))
                .build());
        ApplicationLoadBalancedFargateService service = ApplicationLoadBalancedFargateService.Builder
                .create(this, "GrafanaUIService")
                .taskDefinition(taskDefinition)
                .publicLoadBalancer(true)
                .listenerPort(3000)
                .desiredCount(1)
                .build();
        return service;
    }

    private static SecretsManagerClient createSecretManagerClient() {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.of("eu-central-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")
                ))
                .build();
        return client;
    }

    private String getSecretOfName(String secretName) {
        GetSecretValueResponse response = secretsManagerClient.getSecretValue(
                GetSecretValueRequest.builder()
                        .secretId(secretName)
                        .build()
        );
        return response.secretString();
    }

    public static void main(final String[] args) {
        App app = defineOutputCDKDirectory();
        StackProps props = convertJavaCodeIntoCloudFormationTemplate();
        new LocalStack(app, LOCAL_STACK_NAME, props);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }

    private static App defineOutputCDKDirectory() {
        return new App(AppProps.builder().outdir("./cdk.out").build());
    }

    private static StackProps convertJavaCodeIntoCloudFormationTemplate() {
        return StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();
    }
}
