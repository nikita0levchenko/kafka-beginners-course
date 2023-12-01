ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "kafka-course"
  ).aggregate(kafkaBasics)

lazy val commonSettings = Seq(
  organization := "com.epam",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.13.12"
)

lazy val kafkaBasics = (project in file("kafka-basics")).settings(
  commonSettings
)
