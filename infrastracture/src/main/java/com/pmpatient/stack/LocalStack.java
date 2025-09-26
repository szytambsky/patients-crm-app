package com.pmpatient.stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;

public class LocalStack extends Stack {

    private static final String LOCAL_STACK_NAME = "medtechcrm";

    private final Vpc vpc;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);
        this.vpc = createVpc();
    }

    private Vpc createVpc() {
        return Vpc.Builder.create(this, "PatientManagementVPC")
                .vpcName("PatientManagementVPC")
                .maxAzs(2)
                .build();
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
