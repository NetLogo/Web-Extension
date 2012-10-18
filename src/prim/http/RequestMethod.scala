package org.nlogo.extensions.web.prim.http

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 1:07 PM
 */

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
  def apply(name: String) : Option[RequestMethod] = {
    // `method.getClass.getSimpleName` doesn't work with `object`s.  Grrrrrr... --JAB (10/18/12)
    def extractName(method: RequestMethod) = {
      val ClassNameRegex = """(?:.*)\.(.*?)(?:\$)?""".r
      method.getClass.getName match { case ClassNameRegex(className) => className }
    }
    allMethods find (method => extractName(method).toLowerCase == name.toLowerCase)
  }
}

