package io.conductor.kafka.demo.wikimedia

import com.launchdarkly.eventsource.EventHandler
import com.launchdarkly.eventsource.EventSource
import org.apache.kafka.clients.producer.KafkaProducer

import java.net.URI
import java.util.concurrent.TimeUnit

object WikimediaChangesProducer extends App {

  // Create producer properties
  val bootstrapServer = "127.0.0.1:19092"
  val properties = KafkaUtils.getProperties(bootstrapServer)

  // create producer
  val producer: KafkaProducer[String, String] =
    new KafkaProducer[String, String](properties)

  val topic = "wikimedia.recentchange"

  val eventHandler: EventHandler = new WikimediaChangeHandler(producer, topic)
  val url = "https://stream.wikimedia.org/v2/stream/recentchange"

  val eventSourceBuilder: EventSource.Builder =
    new EventSource.Builder(eventHandler, URI.create(url))
  val eventSource = eventSourceBuilder.build()

  // start the producer in another thread
  eventSource.start()

  // we produce 10 minutes and block thread until then
  TimeUnit.MINUTES.sleep(10)
}
