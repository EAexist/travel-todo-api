#!/bin/bash

sam local start-lambda -t cdk.out/TravelTodoApi-Stg-Stack.template.json
aws lambda invoke --function-name "Handler" --endpoint-url "http://127.0.0.1:3001" --no-verify-ssl --payload '{}' response.json