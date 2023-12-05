name := "kafka-producer-wikimedia"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "3.4.0"
libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.3.5"
)

// https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "4.9.3"

// https://mvnrepository.com/artifact/com.launchdarkly/okhttp-eventsource
libraryDependencies += "com.launchdarkly" % "okhttp-eventsource" % "2.5.0"