#!/usr/bin/env bash

sam build -u
sam package --output-template-file packaged.yaml --s3-bucket speakeasy-deployments
sam deploy --template-file packaged.yaml --stack-name speakeasy-cf --capabilities CAPABILITY_IAM
