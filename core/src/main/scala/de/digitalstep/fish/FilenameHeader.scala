package de.digitalstep.fish

import akka.http.scaladsl.model.headers.{ModeledCustomHeader, ModeledCustomHeaderCompanion}

import scala.util.Try

final class FilenameHeader(filename: String) extends ModeledCustomHeader[FilenameHeader] {
  def renderInRequests = false

  def renderInResponses = false

  val companion = FilenameHeader

  def value: String = filename
}

object FilenameHeader extends ModeledCustomHeaderCompanion[FilenameHeader] {
  override val name = "X-Filename"

  override def parse(value: String) = Try(new FilenameHeader(value))
}
