import sbt._

object Dependencies {

  val Version = "0.1-SNAPSHOT"
  val Service = Seq(
    Libs.`mockito-core` % Test,
    Libs.`scalatest` % Test,
    AkkaHttp.`akka-http`,
    Neo4JDriver.`neo4jJava`,
    Libs.`play-json`,
    Libs.`play-json-extensions`,
    Libs.`akka-http-play-json`,
    Libs.`google-guice`,
    Neo4JDriver.`neo4j-kernel`,
    Neo4JDriver.`neo4j-io`
  )
  val Spark = Seq(
    Libs.`scalatest` % Test,
    SparkLibs.sparkCore,
    SparkLibs.sparkStreaming,
    SparkLibs.sparkSQL,
    SparkLibs.sparkHiveSQL,
    SparkLibs.sparkTestingBase,
    SparkLibs.neo4jSpark
  )

  val alpakkaStreaming = Seq(
    Akka.`akka-actor`,
    Akka.`akka-persistence`,
    Akka.`akka-multi-node-testkit` % Test,
    Alpakka.alpakkaFile,
    Kafka.akkaStreamKafka,
    Kafka.avro
  )
}