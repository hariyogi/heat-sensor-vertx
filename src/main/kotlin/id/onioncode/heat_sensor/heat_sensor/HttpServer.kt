package id.onioncode.heat_sensor.heat_sensor

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.json.JsonObject
import mu.KotlinLogging
import java.net.http.HttpRequest

class HttpServer : AbstractVerticle() {

  val logger = KotlinLogging.logger {  }

  override fun start() {
    vertx.createHttpServer()
      .requestHandler { handler(it) }
      .listen(8080)
  }

  private fun handler(request: HttpServerRequest) {
    if("/" == request.path()) {
      request.response().sendFile("index.html")
    }else if ("/sse" == request.path()) {
      sse(request)
    }else {
      request.response().setStatusCode(404).sendFile("notfound.html")
    }
  }

  private fun sse(request: HttpServerRequest) {
    val response = request.response();
    response.apply {
      putHeader("Content-Type", "text/event-stream")
      putHeader("Cache-Control", "no-cache")
      isChunked = true
    }

    val consumer = vertx.eventBus().consumer<JsonObject>("sensor.updates")
    consumer.handler {
      response.write("event: update\n")
      response.write("data: ${it.body().encode()}\n\n")
    }

    vertx.setPeriodic(1000) {
      vertx.eventBus().request<JsonObject>("sensor.average", "") {
        logger.info { "average : ${it.result().body()}" }
        if(it.succeeded()) {
          response.write("event: average\n")
          response.write("data: ${it.result().body().encode()}\n\n")
        }
      }
    }

    response.endHandler {
      consumer.unregister()
      vertx.setPeriodic(0, null)
    }
  }
}
