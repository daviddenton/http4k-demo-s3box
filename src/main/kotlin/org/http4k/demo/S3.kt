package org.http4k.demo

import io.github.konfigur8.Configuration
import org.http4k.client.ApacheClient
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.AwsAuth
import org.http4k.filter.ClientFilters
import org.http4k.lens.string
import java.io.InputStream

class S3Error(status: Status) : Exception("S3 returned $status")

data class S3File(val key: String, val size: Int) {
    companion object {
        fun parseFiles(value: String) = Regex("""Key>(.+?)</Key""").findAll(value)
            .map { S3File(it.groupValues[1], -1) }.toList()
    }
}

class S3(private val aws: HttpHandler) {

    private val listFiles = Body.string(APPLICATION_XML).map(S3File.Companion::parseFiles).toLens()

    fun list(): List<S3File> =
        aws(Request(GET, "/")).run {
            if (status.successful) listFiles(this) else throw S3Error(status)
        }

    operator fun get(key: String): InputStream? = aws(Request(GET, "/$key")).run {
        if (status == OK) body.stream else throw S3Error(status)
    }

    operator fun set(key: String, content: InputStream) {
        aws(Request(PUT, "/$key").body(content)).run {
            if (!status.successful) throw S3Error(status)
        }
    }

    fun delete(key: String) {
        aws(Request(DELETE, "/$key")).run {
            if (!status.successful) throw S3Error(status)
        }
    }

    companion object {
        private fun SetBucketHost(uri: Uri): Filter = Filter { next ->
            { next(it.uri(it.uri.scheme(uri.scheme).host(uri.host).port(uri.port))) }
        }

        fun configured(config: Configuration) = S3(SetBucketHost(config[Settings.AWS_BUCKET_URL])
            .then(ClientFilters.AwsAuth(config[Settings.S3_CREDENTIALS], config[Settings.AWS_CREDENTIALS]))
            .then(ApacheClient()))
    }
}