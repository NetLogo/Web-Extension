package org.nlogo.extensions.web

import java.io.{ ByteArrayOutputStream, InputStream, OutputStream }

import scala.io.Source
import scala.util.Try

import org.nlogo.api.{ Argument, Command, Reporter, Context, ExtensionException }
import org.nlogo.core.{ LogoList, Primitive }
import org.nlogo.core.Syntax.{ ListType, StringType }
import org.nlogo.extensions.web.requester.http.RequestMethod

trait WebPrimitive {

  self: Primitive =>

  protected def paramify(arg: Argument): Option[Map[String, String]] = Try(arg.getList).toOption.map {
    list: LogoList =>

      def innerListToKV(l: LogoList) =
        try {
          l.toVector.toList map (_.toString) match {
            case a :: b :: Nil => (a, b)
            case _             => throw new ExtensionException("Improperly-sized key-value tuple.")
          }
        } catch {
          case ex: Exception => throw new ExtensionException("Malformed key-value pairs.", ex)
        }

      list.foldLeft(Map[String, String]()) {
        case (acc, x) =>
          val (key, value) = innerListToKV(x.asInstanceOf[LogoList])
          acc + (key -> value)
      }

  }

  protected def httpMethodify(arg: Argument): Option[RequestMethod] = {
    try RequestMethod(arg.getString)
    catch {
      case ex: Exception =>
        System.err.println("Failed to get HTTP method from argument.\n\n" + ex.getMessage)
        None
    }
  }

  protected def processResponse[T](responseTuple: (InputStream, String))(f: (InputStream, String) => T): T = {
    val (response, statusCode) = responseTuple
    try
      f(response, statusCode)
    finally
      response.close()
  }

  protected def responseToLogoList(responseTuple: (InputStream, String)): LogoList =
    processResponse(responseTuple) {
      case (response, statusCode) => LogoList(Source.fromInputStream(response).mkString.trim, statusCode)
    }

  protected def carefully[T](f: => T): T = {
    try f
    catch {
      case ex: Exception => throw new ExtensionException(s"${ex.getMessage}\n\n${ex.getStackTrace}", ex)
    }
  }

  protected def using[A <: { def close() }, B](stream: A)(f: A => B): B =
    try { f(stream) } finally { stream.close() }

}
