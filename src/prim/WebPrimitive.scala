package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, DefaultCommand, DefaultReporter, ExtensionException, LogoList, Primitive }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 4:31 PM
 */

sealed trait WebPrimitive {

  self: Primitive =>

  override def getAgentClassString = "O---"

  protected type ArgsTuple
  protected def  defaultMap:    Map[String, String]

  protected def processArguments(args: Array[Argument]) : ArgsTuple

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
      http.RequestMethod(arg.getString)
    }
    catch {
      case ex: Exception =>
        System.err.println("Failed to get HTTP method from argument.\n\n" + ex.getMessage)
        None
    }
  }

}

abstract class WebCommand  extends DefaultCommand  with WebPrimitive
abstract class WebReporter extends DefaultReporter with WebPrimitive

