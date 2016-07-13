import sbt._

object Dependencies {

  object Version {
    val Slf4J           = "1.7.21"
    val Logback         = "1.1.7"
    val ScalaLogging    = "3.4.0"
    val Akka            = "2.4.8"
    val Circe           = "0.4.1"
    val AkkaHttpCirce   = "1.6.0"
    val ScalaTest       = "2.2.6"
    val SwaggerAkkaHttp = "0.6.2"
  }


  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor"                         % Version.Akka,
    "com.typesafe.akka" %% "akka-http-core"                     % Version.Akka,
    "de.heikoseeberger" %% "akka-http-circe"                    % Version.AkkaHttpCirce
  )

  val circe = Seq("core", "generic", "parser", "java8", "optics") map { x =>
    "io.circe" %% s"circe-$x" % Version.Circe
  }

  val swagger = Seq(
    "com.github.swagger-akka-http" %% "swagger-akka-http" % Version.SwaggerAkkaHttp
  )

  val logging = Seq(
    "com.typesafe.scala-logging" %% "scala-logging"    % Version.ScalaLogging,
    "org.slf4j"                   % "log4j-over-slf4j" % Version.Slf4J,
    "org.slf4j"                   % "jcl-over-slf4j"   % Version.Slf4J,
    "ch.qos.logback"              % "logback-classic"  % Version.Logback
  )

  val scalatest = Seq(
    "org.scalactic" %% "scalactic" % Version.ScalaTest,
    "org.scalatest" %% "scalatest" % Version.ScalaTest % Test
  )

}