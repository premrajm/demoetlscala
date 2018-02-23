import Settings._


lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
  `akka-http-service`,
  `alpakka-streaming`,
  `spark-jobs`
)

val `demoetlscala` = project
  .in(file("."))
  .enablePlugins(DeployApp, DockerPlugin)
  .aggregate(aggregatedProjects: _*)
    .settings(Seq(scalafmtOnCompile:= true,
      coverageEnabled:= true))


//http-service for Neo4J views
lazy val `akka-http-service` = project
  .settings(
    libraryDependencies ++= Dependencies.Service
  )


//spark jobs for putting data in neo4j/hadoop/hbase
lazy val `spark-jobs` = project
  .settings(
    resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven",
    libraryDependencies ++= Dependencies.Spark
  )


//Write side
lazy val `alpakka-streaming` = project
  .settings(
    libraryDependencies ++= Dependencies.alpakkaStreaming
  )