package io.conductor.demos.kafka.consumer

import com.typesafe.scalalogging.Logger
import io.conductor.demos.kafka.utils.KafkaUtils.properties
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException

import java.time.Duration
import scala.jdk.javaapi.CollectionConverters.asJavaCollection
import scala.util.Failure
import scala.util.Success
import scala.util.Try

object ConsumerDemoWithShutdown extends App {

  val logger = Logger(getClass.getSimpleName)
  logger.info(s"${getClass.getSimpleName} starting...")

  val groupId = "my-scala-consumer-application-1"
  val topic = "scala-demo"
  val consumer = new KafkaConsumer[String, String](properties(groupId))

  // get ref to main thread
  val mainThread: Thread = Thread.currentThread

  // add shutdown hook
  Runtime.getRuntime.addShutdownHook(new Thread(() => {
    logger.info(
      "Detected a shutdown, let's exit by calling consumer.wakeup()..."
    )
    consumer.wakeup()

    Try(mainThread.join()) match {
      case Success(_) =>
        logger.info(s"${mainThread.toString} successfully joined")
      case Failure(exception: InterruptedException) =>
        exception.printStackTrace()
    }
  }))

  Try {
    // subscribe a topic
    consumer.subscribe(asJavaCollection(List(topic)))

    // pool for data
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
  } match {
    case Failure(e: WakeupException) => {
      logger.info("Consumer is starting to shutdown")
      closeConsumer(consumer)
    }
    case Failure(e: Exception) => {
      logger.error("Unexpected exception in the consumer", e)
      closeConsumer(consumer)
    }
  }

  def closeConsumer(consumer: KafkaConsumer[String, String]): Unit = {
    consumer.close() // this wil commit offsets
    logger.info("The consumer is gracefully shut down")
  }
}
