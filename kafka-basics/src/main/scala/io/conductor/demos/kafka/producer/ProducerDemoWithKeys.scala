package io.conductor.demos.kafka.producer

import com.typesafe.scalalogging.Logger
import io.conductor.demos.kafka.utils.KafkaUtils
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

object ProducerDemoWithKeys extends App {

  val logger = Logger(getClass.getSimpleName)
  logger.info(s"${getClass.getSimpleName} starting...")

  val topic = "scala-demo"

  // Create producer
  val producer = new KafkaProducer[String, String](KafkaUtils.properties())

  def produce(
      topic: String,
      producer: KafkaProducer[String, String],
      msgCount: Int
  ): Unit =
    for (i <- 0 until msgCount) {

      // Create key
      val key = s"message key ${i % 3}"

      // Create a record
      val record = new ProducerRecord[String, String](
        topic,
        key,
        s"scala message #$i"
      )

      producer.send(
        record,
        (metadata: RecordMetadata, exception: Exception) =>
          if (exception == null)
            logger.info(
              "Received new metadata:\n" +
                s"Timestamp: ${metadata.timestamp()}\n" +
                s"Message key: $key\n" +
                s"Message id: ${metadata.topic()}_${metadata.partition()}_${metadata.offset()}"
            )
          else logger.error("Error while producing", exception)
      )

    }

  produce(topic, producer, 10)

  // flush and close the producer
  // tell the producer send data and block resource until done -- synchronous
  producer.flush()
  producer.close()

}
