package org.nlogo.extensions.web.prim

import
  java.io.InputStream

import
  org.nlogo.api.{ Argument, DefaultCommand, DefaultReporter, Context, ExtensionException, LogoList, Primitive, Syntax },
    Syntax._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 4:31 PM
 */

trait WebPrimitive {

  self: Primitive =>

  override def getAgentClassString = "O---"

  protected type ArgsTuple
  protected def  primArgsSyntax: Array[Int]
  protected def  processArguments(args: Array[Argument]) : ArgsTuple

  protected def defaultMap = Map[String, String]()

  protected def paramify(arg: Argument)      = getList(arg) map listToParams
  protected def getList(arg: Argument)       = try { Option(arg.getList) } catch { case ex: Exception => None }
  protected def listToParams(list: LogoList) = {

    def innerListToKV(l: LogoList) = {
      try {
        l.toVector.toList map (_.toString) match {
          case a :: b :: Nil => (a, b)
          case _             => throw new ExtensionException("Improperly-sized key-value tuple.")
        }
      }
      catch {
        case ex: Exception => throw new ExtensionException("Malformed key-value pairs.", ex)
      }
    }

    list.foldLeft(Map[String, String]()){
      case (acc, x) =>
        val (key, value) = innerListToKV(x.asInstanceOf[LogoList])
        acc + (key -> value)
    }

  }

  protected def httpMethodify(arg: Argument) = {
    try {
      requester.http.RequestMethod(arg.getString)
    }
    catch {
      case ex: Exception =>
        System.err.println("Failed to get HTTP method from argument.\n\n" + ex.getMessage)
        None
    }
  }

  protected def isToString(is: InputStream) = io.Source.fromInputStream(is).mkString.trim
  protected def processResponse[T](responseTuple: (InputStream, String))(f: (InputStream, String) => T) : T = {
    responseTuple match {
      case (response, statusCode) => try { f(response, statusCode) } finally { response.close() }
    }
  }

  protected def responseToLogoList(responseTuple: (InputStream, String)) : LogoList = processResponse(responseTuple) { Tuple2(_, _) match {
    case (response, statusCode) => LogoList(isToString(response), statusCode)
  }}

}

trait CommonWebPrimitive {
  self: WebPrimitive =>
    override protected type ArgsTuple      = (String, requester.http.RequestMethod, Map[String, String])
    override protected def  primArgsSyntax = Array(StringType, StringType, ListType)
    override protected def  processArguments(args: Array[Argument]) : ArgsTuple = {
      val dest      = args(0).getString
      val reqMethod = httpMethodify(args(1)) getOrElse (throw new ExtensionException("Invalid HTTP method name supplied."))
      val params    =      paramify(args(2)) getOrElse defaultMap
      (dest, reqMethod, params)
    }
}

trait SimpleWebPrimitive {
  self: WebPrimitive =>
    override protected type ArgsTuple      = (String)
    override protected def  primArgsSyntax = Array(StringType)
    override protected def  processArguments(args: Array[Argument]) : ArgsTuple = {
      val dest = args(0).getString
      (dest)
    }
}

import DummyImplicit.dummyImplicit

// Using `DummyImplicit` so that the new and old `perform`/`report` methods have distinct signatures after erasure --JAB (10/22/12)
abstract class WebCommand extends DefaultCommand with WebPrimitive {
  override def getSyntax = commandSyntax(primArgsSyntax)
  override def perform(args: Array[Argument], context: Context) { perform(args)(context, dummyImplicit) }
  /*new!*/ def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit)
}

abstract class WebReporter extends DefaultReporter with WebPrimitive {
  override def getSyntax = reporterSyntax(primArgsSyntax, ListType)
  override def report(args: Array[Argument], context: Context) : AnyRef = { report(args)(context, dummyImplicit) }
  /*new!*/ def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef
}

