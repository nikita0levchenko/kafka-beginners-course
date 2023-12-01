package io.conductor.demos.kafka.utils

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

object KafkaUtils {

  // Set properties
  def properties(groupId: String = "default-group-id"): Properties = {
    val properties: Properties = new Properties
    properties.put("bootstrap.servers", "127.0.0.1:19092")
    properties.put(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      new StringSerializer().getClass.getName
    )
    properties.put(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      new StringSerializer().getClass.getName
    )
    properties.put(
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      new StringDeserializer().getClass.getName
    )
    properties.put(
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      new StringDeserializer().getClass.getName
    )
    properties.put(
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
      "earliest"
    )
    properties.put(
      ConsumerConfig.GROUP_ID_CONFIG,
      groupId
    )
    properties.put(
      "partition.asignment.strategy",
      new CooperativeStickyAssignor().getClass.getName
    )
    properties
  }

}
