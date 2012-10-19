package org.nlogo.extensions.web.prim

import org.nlogo.api.{ ExtensionException, Argument, Context, Syntax }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 1:32 PM
 */

object MakeRequest extends WebReporter {

  import Syntax._

  override protected type ArgsTuple  = (String, http.RequestMethod, Map[String, String])
  override protected val  defaultMap = Map[String, String]()

  // Syntax: <prim> destination http_request_method parameter_map
  override def getSyntax = reporterSyntax(Array(StringType, StringType, ListType), ListType)
  override def report(args: Array[Argument], context: Context) : AnyRef = {
     null
  }

  override protected def processArguments(args: Array[Argument]) : ArgsTuple = {
    ("", http.Post, defaultMap) //@
  }

}
