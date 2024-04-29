ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "kafka-course"
  ).aggregate(kafkaBasics, kafkaProducerWikimedia, kafkaConsumerOpensearch, kafkaStreamsWikimedia)

lazy val commonSettings = Seq(
  organization := "com.epam",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.13.12"
)

lazy val kafkaBasics = (project in file("kafka-basics")).settings(
  commonSettings
)

lazy val kafkaProducerWikimedia = (project in file("kafka-producer-wikimedia")).settings(
  commonSettings
)

lazy val kafkaConsumerOpensearch = (project in file("kafka-consumer-opensearch")).settings(
  commonSettings
)

lazy val kafkaStreamsWikimedia = (project in file("kafka-streams-wikimedia")).settings(
  commonSettings
)