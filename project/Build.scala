import sbt._
import Keys._
import scoverage.ScoverageKeys._

object FishBuild extends Build {

  override def settings = super.settings ++ Seq(
    scalaVersion    := "2.11.8",
    coverageEnabled := true
  )

}
