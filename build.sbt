organization in ThisBuild := "de.digitalstep"
version      in ThisBuild := "0.1"
name                      := "fish"

lazy val root = (project in file(".")).aggregate(
    core,
    cluster,
    `actor-remoting`
  )

import Dependencies._

lazy val core = project.
  settings(
    mainClass            := Some("de.digitalstep.fish.Application"),
    libraryDependencies ++= akka ++ circe ++ logging ++ scalatest
  )

lazy val cluster = project.
  settings(
    libraryDependencies ++= akka ++ logging ++ scalatest
  )

lazy val `actor-remoting` = project.
  enablePlugins(JavaAppPackaging).
  settings(SbtMultiJvm.multiJvmSettings: _*).
  settings(
    libraryDependencies              ++=  akka ++ circe ++ logging ++ scalatest,
    compile             in MultiJvm  <<=  (compile in MultiJvm) triggeredBy (compile in Test),
    parallelExecution   in Test       :=  true,
    executeTests        in Test      <<=  (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults)  =>
        val overall = if (testResults.overall.id < multiNodeResults.overall.id)
          multiNodeResults.overall
        else
          testResults.overall
        Tests.Output(overall,
                    testResults.events    ++ multiNodeResults.events,
                    testResults.summaries ++ multiNodeResults.summaries)
    }
  ).
  configs(MultiJvm)
