package io.conductor.demos.kafkaopensearch.utils

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

object KafkaUtils {

  def getProperties(
      bootstrapServer: String,
      groupId: String = "default-group-id"
  ): Properties = {
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
      "latest"
    )
    properties.put(
      ConsumerConfig.GROUP_ID_CONFIG,
      groupId
    )

    // set high throughput producer configs
    properties.put(
      ProducerConfig.LINGER_MS_CONFIG,
      "20"
    )
    properties.put(
      ProducerConfig.BATCH_SIZE_CONFIG,
      (32 * 1024).toString
    )
    properties.put(
      ProducerConfig.COMPRESSION_TYPE_CONFIG,
      "snappy"
    )

    // enable or disable auto.commit. true by default
//    properties.put(
//      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
//      "false"
//    )
    properties
  }

}
