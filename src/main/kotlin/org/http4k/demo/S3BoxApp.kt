package org.http4k.demo

import io.github.konfigur8.Configuration
import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.ContentType.Companion.OCTET_STREAM
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.SEE_OTHER
import org.http4k.core.with
import org.http4k.lens.Header.Common.CONTENT_TYPE
import org.http4k.lens.MultipartFormFile
import org.http4k.lens.Path
import org.http4k.lens.Validator
import org.http4k.lens.multipartForm
import org.http4k.lens.string
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel


data class ListFiles(val files: List<S3File>) : ViewModel

object Index {
    private val templates = HandlebarsTemplates().CachingClasspath()
    private val htmlBody = Body.string(ContentType.TEXT_HTML).toLens()

    operator fun invoke(s3: S3) = { _: Request ->
        Response(OK).with(htmlBody of templates(ListFiles(s3.list())))
    }
}

object Upload {
    private val files = MultipartFormFile.multi.required("files")
    private val form = Body.multipartForm(Validator.Strict, files).toLens()

    operator fun invoke(s3: S3): HttpHandler = { req ->
        files(form(req)).forEach {
            s3[it.filename] = it.content
        }
        Response(SEE_OTHER).header("location", "/")
    }
}

object Get {
    private val id = Path.of("id")

    operator fun invoke(s3: S3) = { req: Request ->
        s3[id(req)]?.let { Response(OK)
            .with(CONTENT_TYPE of OCTET_STREAM)
            .body(it) } ?: Response(NOT_FOUND)
    }
}

object Delete {
    private val id = Path.of("id")

    operator fun invoke(s3: S3) = { req: Request ->
        s3.delete(id(req))
        Response(SEE_OTHER).header("location", "/")
    }
}

object S3BoxApp {
    operator fun invoke(config: Configuration): RoutingHttpHandler {
        val s3 = S3.configured(config)

        return routes(
            "/{id}/delete" bind POST to Delete(s3),
            "/{id}" bind GET to Get(s3),
            "/" bind routes(
                POST to Upload(s3),
                GET to Index(s3)
            )
        )
    }
}