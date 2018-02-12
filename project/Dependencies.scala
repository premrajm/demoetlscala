import sbt._

object Dependencies {

  val Version = "0.1-SNAPSHOT"
  val Service = Seq(
    Libs.`mockito-core` % Test,
    Libs.`scalatest` % Test,
    AkkaHttp.`akka-http`
//    SparkLibs.`spark-sql`
  )
}