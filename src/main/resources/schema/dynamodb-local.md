# DynamoDB

#### brew install awscli

#### brew install --cask dynamodb-local


## start local server
eragapati@Ravis-Air ~ % dynamodb-local     
Initializing DynamoDB Local with the following configuration:
Port:	8000
InMemory:	false
DbPath:	null
SharedDb:	false
shouldDelayTransientStatuses:	false
CorsParams:	null


## CONFIG
eragapati@Ravis-Air ~ % aws configure
AWS Access Key ID [None]: fakeAccessKey
AWS Secret Access Key [None]: fakeSecretAccessKey
Default region name [None]: us-east-2
Default output format [None]: json


#### export AWS_ACCESS_KEY_ID=fakeAccessKey
#### export AWS_SECRET_ACCESS_KEY=fakeSecretAccessKey


## CREATE TABLE

aws dynamodb create-table \
--table-name movie_details \
--attribute-definitions AttributeName=id,AttributeType=S \
--key-schema AttributeName=id,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
--region us-east-2 \
--output json \
--endpoint-url http://localhost:8000
