package io.conductor.demos.kafka.consumer

import com.typesafe.scalalogging.Logger
import io.conductor.demos.kafka.utils.KafkaUtils.properties
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.time.Duration
import scala.jdk.javaapi.CollectionConverters.asJavaCollection

object ConsumerDemo extends App {

  val logger = Logger(getClass.getSimpleName)
  logger.info(s"${getClass.getSimpleName} starting...")

  val groupId = "my-scala-consumer-application"
  val topic = "scala-demo"
  val consumer = new KafkaConsumer[String, String](properties(groupId))
  consumer.subscribe(asJavaCollection(List(topic)))

  while (true) {
    logger.info("polling...")
    val records = consumer.poll(Duration.ofMillis(1000))
    records.forEach(record =>
      logger.info(
        "Key: " + record.key + "\n" +
          "Value: " + record.value + "\n" +
          "Id: " + record.topic + "_" + record.partition + "_" + record.offset
      )
    )
  }

}
