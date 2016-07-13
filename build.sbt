import Dependencies._

name := "fish"
version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= akka ++ circe ++ logging ++ scalatest
