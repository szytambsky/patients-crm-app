package com.pmpatient.stack;

import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class LocalStack extends Stack {

    private static final String LOCAL_STACK_NAME = "medtechcrm";

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);
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
