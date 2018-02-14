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
    SparkLibs.sparkCore,
    SparkLibs.sparkStreaming,
    SparkLibs.sparkSQL,
    SparkLibs.sparkHiveSQL
  )

  val Integration = Seq(
    Akka.`akka-actor`,
    Akka.`akka-persistence`,
    Akka.`akka-multi-node-testkit` % Test,
    Alpakka.alpakkaFtp,
    Kafka.akkaStreamKafka
  )
}