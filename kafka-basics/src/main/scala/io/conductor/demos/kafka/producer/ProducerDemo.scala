package io.conductor.demos.kafka.producer

import com.typesafe.scalalogging.Logger
import io.conductor.demos.kafka.utils.KafkaUtils.properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

object ProducerDemo extends App {

  val logger = Logger(getClass.getSimpleName)
  logger.info(s"${getClass.getName} starting...")

  // create producer
  val producer = new KafkaProducer[String, String](properties())

  // create a producer record
  val record =
    new ProducerRecord[String, String](
      "scala-demo",
      "Hello world from scala #2"
    )

  // send data
  producer.send(record)

  // flush and close the producer
  // tell the producer send data and block resource until done -- synchronous
  producer.flush()
  producer.close()

}
