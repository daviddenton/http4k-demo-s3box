package org.http4k.demo

import io.github.konfigur8.ConfigurationTemplate
import io.github.konfigur8.Property
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.core.Credentials
import org.http4k.core.Uri

object Settings {
    val CREDENTIALS = Property("CREDENTIALS", String::toCredentials, Credentials::fromCredentials)
    val S3_CREDENTIALS = Property("S3_REGION", { AwsCredentialScope(it, "s3") }, AwsCredentialScope::region)
    val AWS_CREDENTIALS = Property("AWS_CREDENTIALS", String::toAwsCredentials, AwsCredentials::fromCredentials)
    val AWS_BUCKET_URL = Property("AWS_BUCKET", { Uri.of("https://$it.s3.amazonaws.com")})

    val defaults = ConfigurationTemplate()
        .requiring(CREDENTIALS)
        .requiring(S3_CREDENTIALS)
        .requiring(AWS_CREDENTIALS)
        .requiring(AWS_BUCKET_URL)
}

fun AwsCredentials.fromCredentials() = "$accessKey:$secretKey"
fun String.toAwsCredentials() = split(":").run { AwsCredentials(get(0), get(1)) }
fun Credentials.fromCredentials() = "$user:$password"
fun String.toCredentials() = split(":").run { Credentials(get(0), get(1)) }
