#!/bin/bash
set -e # Stops the script if any command fails

aws secretsmanager create-secret \
  --name jwtSecret \
  --secret-string 'JWT_SECRET from env' \
  --endpoint-url http://localhost:4566

aws secretsmanager list-secrets --endpoint-url http://localhost:4566


