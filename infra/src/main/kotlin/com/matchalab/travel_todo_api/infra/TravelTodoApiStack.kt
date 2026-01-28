//https://docs.aws.amazon.com/ko_kr/lambda/latest/dg/lambda-cdk-tutorial.html#lambda-cdk-step-2
package com.matchalab.travel_todo_api.infra

import software.amazon.awscdk.*
import software.amazon.awscdk.services.lambda.*
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.logs.LogGroup
import software.amazon.awscdk.services.logs.RetentionDays
import software.amazon.awscdk.services.ssm.StringParameter
import software.constructs.Construct
import java.io.File
import kotlin.String
import kotlin.to


class TravelTodoApiStack(scope: Construct?, id: String?, props: StackProps?, env: String = "stg") :
    Stack(scope, id, props) {
    constructor(scope: Construct?, id: String?) : this(scope, id, null)

    init {

        Tags.of(this).add("Environment", env)
        Tags.of(this).add("Application", "TravelTodoApi")

        val functionName = "travel-todo-api-handler-$env"

        val webAdapterLayer = LayerVersion.fromLayerVersionArn(
            this, "WebAdapterLayer",
            "arn:aws:lambda:ap-northeast-2:753240598075:layer:LambdaAdapterLayerArm64:25"
        )
//        val ssmExtension: ILayerVersion = LayerVersion.fromLayerVersionArn(
//            this,
//            "SsmExtension",
//            "arn:aws:lambda:${this.region}:187925254637:layer:AWS-Parameters-and-Secrets-Lambda-Extension:11"
//        )

        val lambdaLogGroup = LogGroup.Builder.create(this, "LambdaLogGroup")
            .logGroupName("/aws/lambda/$functionName")
            .retention(RetentionDays.ONE_WEEK)
            .removalPolicy(RemovalPolicy.DESTROY)
            .build()

        val artifactPath = File("..", "build/distributions/travel-todo-api-0.0.1-SNAPSHOT.zip").path

        val SPRING_PROFILES_ACTIVE = "prod"

        // Database
        val SPRING_DATASOURCE_URL =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/SPRING_DATASOURCE_URL")
        val SPRING_DATASOURCE_USERNAME =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/SPRING_DATASOURCE_USERNAME")
        val SPRING_DATASOURCE_PASSWORD =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/SPRING_DATASOURCE_PASSWORD")

        // Google Cloud
        val GOOGLE_CLOUD_PROJECT =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/GOOGLE_CLOUD_PROJECT")
        val SPRING_AI_VERTEX_AI_GEMINI_LOCATION =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/SPRING_AI_VERTEX_AI_GEMINI_LOCATION")
        val appGoogleClientId =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/APP_GOOGLE_CLIENT_ID")
        val appGoogleWebClientId =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/APP_GOOGLE_WEB_CLIENT_ID")

        val APP_GOOGLE_PLACES_API_KEY = StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/APP_GOOGLE_PLACES_API_KEY")
        val APP_GOOGLE_PLACES_BASE_URL = StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/APP_GOOGLE_PLACES_BASE_URL")


        //  app
        val appSecurityAdminEmails =
            StringParameter.valueForStringParameter(this, "/stg/travel-todo-api/APP_SECURITY_ADMIN_EMAILS")
        val appCorsAllowedOrigins = StringParameter.valueForStringParameter(
            this, "/stg/travel-todo-api/APP_CORS_ALLOWED_ORIGINS"
        )

        val handler = Function.Builder.create(this, "Handler")
//            .reservedConcurrentExecutions(10)
            .functionName(functionName)
            .runtime(Runtime.JAVA_21)
            .architecture(Architecture.ARM_64) // Cost-effective than X86
            .snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
            .memorySize(2048) // Required for Spring Boot performance
            .timeout(Duration.minutes(3))
            .handler("run.sh")
            .code(
                Code.fromAsset(artifactPath)
            )
            .layers(listOf(webAdapterLayer))
            .logGroup(lambdaLogGroup)
            .environment(
                mapOf(
                    // profile
                    "SPRING_PROFILES_ACTIVE" to SPRING_PROFILES_ACTIVE,

                    // frontend
                    "APP_CORS_ALLOWED_ORIGINS" to appCorsAllowedOrigins,

                    // database
                    "SPRING_DATASOURCE_URL" to SPRING_DATASOURCE_URL,
                    "SPRING_DATASOURCE_USERNAME" to SPRING_DATASOURCE_USERNAME,
                    "SPRING_DATASOURCE_PASSWORD" to SPRING_DATASOURCE_PASSWORD,

                    // google cloud
                    "GOOGLE_CLOUD_PROJECT" to GOOGLE_CLOUD_PROJECT,
                    "APP_GOOGLE_CLIENT_ID" to appGoogleClientId,
                    "APP_GOOGLE_WEB_CLIENT_ID" to appGoogleWebClientId,
                    "SPRING_AI_VERTEX_AI_GEMINI_LOCATION" to SPRING_AI_VERTEX_AI_GEMINI_LOCATION,
                    "SPRING_CLOUD_GCP_CREDENTIALS_LOCATION" to "/tmp/gcp-wif-config.json",
                    "GOOGLE_APPLICATION_CREDENTIALS" to "/tmp/gcp-wif-config.json",

                    // app
                    "APP_SECURITY_ADMIN_EMAILS" to appSecurityAdminEmails,
                    "APP_GOOGLE_PLACES_API_KEY" to APP_GOOGLE_PLACES_API_KEY,
                    "APP_GOOGLE_PLACES_BASE_URL" to APP_GOOGLE_PLACES_BASE_URL,

                    // web adapter layer
                    "AWS_LWA_ASYNC_INIT" to "false",
                    "AWS_LWA_INVOKE_MODE" to "buffered",
                    "AWS_LAMBDA_EXEC_WRAPPER" to "/opt/bootstrap",
                    "PORT" to "8080",

                    // SnapStart
                    "READINESS_CHECK_PATH" to "/actuator/health/readiness",
                    "READINESS_CHECK_PORT" to "8080",
                    "READINESS_CHECK_TIMEOUT_MS" to "30000",
                )
            )
            .build()

        val version = handler.currentVersion

        val alias = Alias.Builder.create(this, "LiveAlias")
            .aliasName("live")
            .version(version)
            .build()

        val fnUrl = alias.addFunctionUrl(
            FunctionUrlOptions.builder()
                .authType(FunctionUrlAuthType.NONE)
//                .invokeMode(FunctionUrlInvokeMode.BUFFERED)
                .build()
        )

        CfnOutput.Builder.create(this, "ApiUrl").value(fnUrl.url).build()
    }
}