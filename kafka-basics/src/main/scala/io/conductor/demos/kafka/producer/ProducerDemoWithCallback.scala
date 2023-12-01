package io.conductor.demos.kafka.producer

import com.typesafe.scalalogging.Logger
import io.conductor.demos.kafka.utils.KafkaUtils
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

object ProducerDemoWithCallback extends App {

  val logger = Logger(getClass.getSimpleName)
  logger.info(s"${getClass.getSimpleName} starting...")

  // create producer
  val producer = new KafkaProducer[String, String](KafkaUtils.properties())

  // Create a record
  val record = new ProducerRecord[String, String](
    "scala-demo",
    "hello from scala with callbacks"
  )

  // send data with callback
  producer.send(
    record,
    (metadata: RecordMetadata, exception: Exception) =>
      if (exception == null)
        logger.info(
          "Received new metadata:\n" +
            s"Timestamp: ${metadata.timestamp()}\n" +
            s"Message id: ${metadata.topic()}_${metadata.partition()}_${metadata.offset()}"
        )
      else logger.error("Error while producing", exception)
  )

  // flush and close the producer
  // tell the producer send data and block resource until done -- synchronous
  producer.flush()
  producer.close()

}
