package id.onioncode.heat_sensor.heat_sensor

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Promise

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(
      "id.onioncode.heat_sensor.heat_sensor.HeatSensor",
      DeploymentOptions().setInstances(10)
    )
    vertx.deployVerticle(Listener())
    vertx.deployVerticle(SensorData())
    vertx.deployVerticle(HttpServer())
  }
}
