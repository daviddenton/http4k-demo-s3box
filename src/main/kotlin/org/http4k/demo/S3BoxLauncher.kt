package org.http4k.demo

import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main(args: Array<String>) {
    val port = if (args.isNotEmpty()) args[0].toInt() else 5000
    S3BoxApp(Settings.defaults.reify()).asServer(Jetty(port)).start().block()
}
