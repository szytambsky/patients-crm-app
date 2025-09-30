#!/bin/bash
set -e
# create localstack with auth key and launch it, then connect localstack desktop client to your account then create jwtSecret:
aws secretsmanager create-secret \
  --name jwtSecret \
  --secret-string '<JWT_SECRET>' \
  --endpoint-url http://localhost:4566

# check if jwtSecret is successfully created on running stack
aws secretsmanager list-secrets --endpoint-url http://localhost:4566

# run LocalStack.java file with @argfile(Java 9+) and then deploy stack with ./localstack-deploy.sh inside infrastructure folder
# if you failed to create/update the stack of localstack, then log error on stout to file
aws --endpoint-url=http://localhost:4566 cloudformation describe-stack-events --stack-name patient-management > localstack-error.txt

# detailed info of certain secret from list-secrets -> "Name"
aws --endpoint-url=http://localhost:4566 secretsmanager get-secret-value \
    --secret-id <Name> # <Name> from list-secrets