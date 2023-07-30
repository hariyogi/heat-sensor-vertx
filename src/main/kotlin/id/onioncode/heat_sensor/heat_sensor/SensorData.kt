package id.onioncode.heat_sensor.heat_sensor

import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import mu.KotlinLogging
import java.util.function.BiConsumer
import java.util.stream.Collectors
import kotlin.math.log

class SensorData : AbstractVerticle() {

  private val logger = KotlinLogging.logger {  }
  private val lastValues = mutableMapOf<String, Double>()

  override fun start() {
    val bus = vertx.eventBus()
    bus.consumer("sensor.updates", ::update)
    bus.consumer("sensor.average", ::average)
  }

  private fun update(message: Message<JsonObject>) {
    val body = message.body()
    lastValues[body.getString("id")] = body.getDouble("temp")
  }

  private fun average(message: Message<JsonObject>) {
    val avg = lastValues.values.stream().collect(Collectors.averagingDouble(Double::toDouble))
    logger.info { "Temp Average $avg" }
    val json = json {
      obj(
        "average" to avg
      )
    }
    message.reply(json)
  }
}
