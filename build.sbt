import Dependencies._

name := "fish"

lazy val core = project.
  settings(
    libraryDependencies ++= akka ++ circe ++ logging ++ scalatest
  )

lazy val `actor-remoting` = project.
  settings(SbtMultiJvm.multiJvmSettings: _*).
  settings(
    libraryDependencies             ++= akka ++ circe ++ logging ++ scalatest,
    compile             in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    parallelExecution   in Test      := true,
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults)  =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
        Tests.Output(overall,
                     testResults.events ++ multiNodeResults.events,
                     testResults.summaries ++ multiNodeResults.summaries)
    }
  ).
  configs(MultiJvm)
