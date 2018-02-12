import Settings._

val `demoetlscala` = project
  .in(file("."))
  .enablePlugins(DeployApp, DockerPlugin)
  .settings(defaultSettings: _*)
  .settings(
    libraryDependencies ++= Dependencies.Service
  )


//Logging service
lazy val `akka-http-service` = project
  .settings(
    libraryDependencies ++= Dependencies.Service
  )
