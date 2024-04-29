package io.conductor.demos.kafkaopensearch

import com.google.gson.JsonParser
import com.typesafe.scalalogging.Logger
import io.conductor.demos.kafkaopensearch.utils.KafkaUtils
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.opensearch.action.bulk.BulkRequest
import org.opensearch.action.index.IndexRequest
import org.opensearch.client.RequestOptions
import org.opensearch.client.RestClient
import org.opensearch.client.RestHighLevelClient
import org.opensearch.client.indices.CreateIndexRequest
import org.opensearch.client.indices.GetIndexRequest
import org.opensearch.common.xcontent.XContentType

import java.net.URI
import java.time.Duration
import java.util.Properties
import scala.jdk.javaapi.CollectionConverters.asJavaCollection
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.util.Using

object OpenSearchConsumer extends App {

  private def extreactIdFromJSON(json: String) =
    JsonParser
      .parseString(json)
      .getAsJsonObject
      .get("meta") // data specific field
      .getAsJsonObject
      .get("id")
      .getAsString

  def createIndex(
      indexName: String,
      logger: Logger,
      client: RestHighLevelClient
  ) = Using((client)) { client =>
    if (
      !client
        .indices()
        .exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)
    ) {
      val createIndexRequest: CreateIndexRequest =
        new CreateIndexRequest(indexName)
      client.indices().create(createIndexRequest, RequestOptions.DEFAULT)
      logger.info(s"The $indexName index has been created!")
    } else
      logger.info(s"The $indexName index already exist!")
  }

  def createIndex(
      indexName: String,
      logger: Logger,
      client: RestHighLevelClient,
      consumer: KafkaConsumer[String, String]
  ) = Using.resources(client, consumer) { (client, consumer) =>
    if (
      !client
        .indices()
        .exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)
    ) {
      val createIndexRequest: CreateIndexRequest =
        new CreateIndexRequest(indexName)
      client.indices().create(createIndexRequest, RequestOptions.DEFAULT)
      logger.info(s"The $indexName index has been created!")
    } else
      logger.info(s"The $indexName index already exist!")

    // subscribe consumer to topic
    consumer.subscribe(asJavaCollection(List("wikimedia.recentchange")))

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
      while (true) {
        val records = consumer.poll(Duration.ofMillis(3000))
        logger.info(s"Received ${records.count()} record(s)")

        // bulk request
        val bulkRequest = new BulkRequest()

        records.forEach { record =>
          // send the record into OpenSearch

          // How to make out consumer idempotent

          // Strategy 1: Define an ID using kafka coordinates
          val kafkaId =
            s"${record.topic()}_${record.partition()}_${record.offset()}"

          // Strategy 2: If your data contains id - use this id
          // We will extract id from JSON value
          val messageId = extreactIdFromJSON(record.value())

          val indexRequest =
            new IndexRequest(indexName)
              .source(record.value(), XContentType.JSON)
              .id(messageId)
          //.id(kafkaId)

          //val response = client.index(indexRequest, RequestOptions.DEFAULT)
          bulkRequest.add(indexRequest)

          if (bulkRequest.numberOfActions() == 500) {
            val bulkResponse =
              openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT)
            logger.info(s"Inserted ${bulkResponse.getItems.length} record(s)")
          }

          //logger.info(s"Response id: ${response.getId}")

        }

        // commit offsets manually
//      consumer.commitSync()
//      logger.info("offsets have been committed!")
      }
    } match {
      case Success(value) => value
      case Failure(e: WakeupException) => {
        logger.info("Consumer is starting to shutdown")
        closeConsumer(consumer)
      }
      case Failure(e: Exception) => {
        logger.error("Unexpected exception in the consumer", e)
        closeConsumer(consumer)
      }
    }
  }

  def closeConsumer(consumer: KafkaConsumer[String, String]): Unit = {
    consumer.close() // this wil commit offsets
    logger.info("The consumer is gracefully shut down")
  }

  def createOpenSearchClient(connString: String): RestHighLevelClient = {
    // we build a URI from a connection string
    val connUri = URI.create(connString)

    // extract login information if exist
    val userInfo = connUri.getUserInfo

    if (userInfo == null) {
      // REST client with security
      new RestHighLevelClient(
        RestClient.builder(
          new HttpHost(connUri.getHost, connUri.getPort, "http")
        )
      )
    } else {
      // REST client with security
      val auth = userInfo.split(":")
      val cp: CredentialsProvider = new BasicCredentialsProvider()
      cp.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(auth(0), auth(1))
      )
      new RestHighLevelClient(
        RestClient
          .builder(
            new HttpHost(connUri.getHost, connUri.getPort, connUri.getScheme)
          )
          .setHttpClientConfigCallback(httpAsyncClientBuilder =>
            httpAsyncClientBuilder
              .setDefaultCredentialsProvider(cp)
              .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
          )
      )
    }

  }

  def createKafkaConsumer(
      properties: Properties
  ) = new KafkaConsumer[String, String](properties)

  val logger = Logger(getClass.getSimpleName)
  logger.info(s"${getClass.getSimpleName} starting...")

  // first create an OpenSearch client
  val connString = "http://localhost:9200"
  val openSearchClient = createOpenSearchClient(connString)

  // create our kafka consumer
  val bootstrapServer = "127.0.0.1:19092"
  val groupId = "consumer-opensearch-demo"
  val properties = KafkaUtils.getProperties(bootstrapServer, groupId)
  val consumer = createKafkaConsumer(properties)

  // we need to create the index on OpenSearch if it doesn't exist already
  createIndex("wikimedia", logger, openSearchClient, consumer)

  // main logic

  // close things

}
