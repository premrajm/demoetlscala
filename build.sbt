import Settings._

val `demoetlscala` = project
  .in(file("."))
  .enablePlugins(DeployApp, DockerPlugin)
  .settings(defaultSettings: _*)
  .settings(
    libraryDependencies ++= Dependencies.Service
  )


//http-service for Neo4J views
lazy val `akka-http-service` = project
  .settings(
    libraryDependencies ++= Dependencies.Service
  )


//spark jobs for putting data in neo4j/hadoop/hbase
lazy val `spark-jobs` = project
  .settings(
    libraryDependencies ++= Dependencies.Spark
  )

//Write side
lazy val `integration` = project
  .settings(
    libraryDependencies ++= Dependencies.Integration
  )