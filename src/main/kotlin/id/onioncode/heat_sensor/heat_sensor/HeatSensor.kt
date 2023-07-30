package id.onioncode.heat_sensor.heat_sensor

import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import mu.KotlinLogging
import java.util.*

class HeatSensor : AbstractVerticle() {

  private val logger = KotlinLogging.logger {  }

  private val random = Random()
  private val sensorId = UUID.randomUUID().toString()
  private var temperature = 21.0

  override fun start() {
    logger.info { "Heat Sensor deploy success" }
    scheduleNextUpdate()
  }

  private fun scheduleNextUpdate() {
    vertx.setTimer(random.nextInt(5000) + 1000L) { update() }
  }

  private fun update() {
    temperature += (delta() / 10)
    val payload = json {
      obj(
        "id" to sensorId,
        "temp" to temperature
      )
    }

    vertx.eventBus().publish("sensor.updates", payload)
    scheduleNextUpdate()
  }

  private fun delta(): Double {
    return if (random.nextInt() > 0) {
      random.nextGaussian()
    } else {
      -random.nextGaussian()
    }
  }
}
