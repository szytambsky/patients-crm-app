package com.pmpatient.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    // GRPC localhost:9090/BillingService/CreateBillingAccount
    public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String billingServerAddress,
                                    @Value("${billing.service.grpc.port:9091}") int billingServerPort) {
        log.info("Connecting to Billing Service GRPC service at {}:{}",
                billingServerAddress, billingServerPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(billingServerAddress, billingServerPort).usePlaintext().build();
        this.blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    @CircuitBreaker(name = "billingService", fallbackMethod = "billingFallback")
    @Retry(name = "billingRetry")
    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();
        BillingResponse billingResponse = blockingStub.createBillingAccount(billingRequest);
        log.info("Received response from billing service via GRPC: {}", billingResponse);
        return billingResponse;
    }
}
