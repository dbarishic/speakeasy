Push-Location $PSScriptRoot

sam build

if ($?) {
    sam package --output-template-file packaged.yaml --s3-bucket speakeasy-deployments
}

if ($?) {
    sam deploy --template-file packaged.yaml --stack-name speakeasy-cf --capabilities CAPABILITY_IAM
}