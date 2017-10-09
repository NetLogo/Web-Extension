package org.nlogo.extensions.web.requester.http

sealed trait RequestMethod

object Delete  extends RequestMethod
object Get     extends RequestMethod
object Head    extends RequestMethod
object Options extends RequestMethod
object Patch   extends RequestMethod
object Post    extends RequestMethod
object Put     extends RequestMethod
object Trace   extends RequestMethod

object RequestMethod {
  private val allMethods = List(Delete, Get, Head, Options, Patch, Post, Put, Trace)
  def apply(name: String): Option[RequestMethod] = {
    def extractName(method: RequestMethod): String = {
      val ClassNameRegex = """(?:.*)\.(.*?)(?:\$)?""".r
      method.getClass.getName match { case ClassNameRegex(className) => className }
    }
    allMethods.find(method => extractName(method).toLowerCase == name.toLowerCase)
  }
}

