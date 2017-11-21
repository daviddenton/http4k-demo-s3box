package org.http4k.demo

import io.github.konfigur8.ConfigurationTemplate
import io.github.konfigur8.Property
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.core.Uri

object Settings {
    val S3_CREDENTIALS = Property("S3_REGION", { AwsCredentialScope(it, "s3") }, AwsCredentialScope::region)
    val AWS_CREDENTIALS = Property("AWS_CREDENTIALS", String::toCredentials, AwsCredentials::fromCredentials)
    val AWS_BUCKET_URL = Property("AWS_BUCKET", { Uri.of("https://$it.s3.amazonaws.com")})

    val defaults = ConfigurationTemplate()
        .requiring(S3_CREDENTIALS)
        .requiring(AWS_CREDENTIALS)
        .requiring(AWS_BUCKET_URL)
}

fun AwsCredentials.fromCredentials() = "$accessKey:$secretKey"
fun String.toCredentials() = split(":").run { AwsCredentials(get(0), get(1)) }
