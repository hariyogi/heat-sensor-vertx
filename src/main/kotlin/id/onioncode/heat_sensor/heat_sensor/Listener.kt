package id.onioncode.heat_sensor.heat_sensor

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import mu.KotlinLogging
import java.text.DecimalFormat

class Listener : AbstractVerticle() {

  private val logger = KotlinLogging.logger {  }
  private val format = DecimalFormat("##.#")

  override fun start() {
    logger.info { "Listener deploy success" }
    val eventBus = vertx.eventBus()
    eventBus.consumer<JsonObject>("sensor.updates") {
      val body = it.body()
      val id = body.getString("id")
      val temperature = format.format(body.getDouble("temp"))
      logger.info { "$id reports a temperature ~${temperature}C" }
    }
  }
}
