package io.conductor.kafka.demo.wikimedia

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.MessageEvent
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord

class WikimediaChangeHandler(
    kafkaProducer: KafkaProducer[String, String],
    topic: String
) extends EventHandler {

  private val logger = Logger(getClass.getSimpleName)

  override def onOpen(): Unit = () // nothing to do here

  override def onClosed(): Unit = kafkaProducer.close()

  override def onMessage(event: String, messageEvent: MessageEvent): Unit = {
    // asynchronous code
    logger.info(s"${messageEvent.getData} sending into $topic")
    kafkaProducer.send(
      new ProducerRecord[String, String](topic, messageEvent.getData)
    )
  }

  override def onComment(comment: String): Unit = () // nothing to do here

  override def onError(t: Throwable): Unit =
    logger.error("Error in stream reding", t)
}
