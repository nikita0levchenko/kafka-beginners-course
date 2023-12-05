package io.conductor.kafka.demo.wikimedia.utils

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

object KafkaUtils {

  def getProperties(bootstrapServer: String): Properties = {
    val properties: Properties = new Properties
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    properties.put(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      new StringSerializer().getClass.getName
    )
    properties.put(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      new StringSerializer().getClass.getName
    )
    properties
  }

}
