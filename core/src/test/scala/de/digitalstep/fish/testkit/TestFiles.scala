package de.digitalstep.fish.testkit

import scala.io

/**
  * load test files
  */
object TestFiles {

  /**
    * Loads a classpath resource
    *
    * @param path classpath local path
    * @return the file content as String
    */
  def content(path: String) = io.Source.fromURL(getClass.getResource(path)).mkString

}
