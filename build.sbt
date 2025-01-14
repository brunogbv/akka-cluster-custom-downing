name := "akka-cluster-custom-downing"

organization := "com.github.TanUkkii007"

homepage := Some(url("https://github.com/TanUkkii007/akka-cluster-custom-downing"))

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.8", "2.12.0")

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-encoding", "UTF-8",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-language:higherKinds"
)

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

val akkaVersion = "2.5.23"

libraryDependencies ++= Seq(
  "com.typesafe.akka"     %% "akka-actor"               % akkaVersion,
  "com.typesafe.akka"     %% "akka-cluster"             % akkaVersion,
  "com.typesafe.akka"     %% "akka-multi-node-testkit"  % akkaVersion % Test,
  "org.scalatest"         %% "scalatest"                % "3.0.4"     % Test
)

compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test)

parallelExecution in Test := false

executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
  case (testResults, multiNodeResults) =>
    val overall =
      if (testResults.overall.id < multiNodeResults.overall.id)
        multiNodeResults.overall
      else
        testResults.overall
    Tests.Output(overall,
      testResults.events ++ multiNodeResults.events,
      testResults.summaries ++ multiNodeResults.summaries)
}

configs(MultiJvm)

BintrayPlugin.autoImport.bintrayPackage := "akka-cluster-custom-downing"

enablePlugins(BintrayPlugin, ReleasePlugin)

releaseCrossBuild := true
