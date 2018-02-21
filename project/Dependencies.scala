import sbt._

object Dependencies {

  val Version = "0.1-SNAPSHOT"
  val Service = Seq(
    Libs.`mockito-core` % Test,
    Libs.`scalatest` % Test,
    AkkaHttp.`akka-http`,
    Neo4JDriver.`neo4jJava`
  )
  val Spark = Seq(
    Libs.`scalatest` % Test,
    SparkLibs.sparkCore,
    SparkLibs.sparkStreaming,
    SparkLibs.sparkSQL,
    SparkLibs.sparkHiveSQL,
    SparkLibs.sparkTestingBase
  )

  val alpakkaStreaming = Seq(
    Akka.`akka-actor`,
    Akka.`akka-persistence`,
    Akka.`akka-multi-node-testkit` % Test,
    Alpakka.alpakkaFile,
    Kafka.akkaStreamKafka
  )
}