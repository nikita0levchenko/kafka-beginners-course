name := "kafka-consumer-opensearch"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.4.0"
libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.3.5"
)

// https://mvnrepository.com/artifact/org.opensearch.client/opensearch-rest-high-level-client
libraryDependencies += "org.opensearch.client" % "opensearch-rest-high-level-client" % "1.2.4"

// https://mvnrepository.com/artifact/com.google.code.gson/gson
libraryDependencies += "com.google.code.gson" % "gson" % "2.9.0"