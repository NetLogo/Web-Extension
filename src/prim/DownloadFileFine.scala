package org.nlogo.extensions.web.prim

import
  org.nlogo.api.{ Argument, Context, ExtensionException, Syntax },
    Syntax.{ ListType, StringType }

import
  util.FileWriter

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 12/11/12
 * Time: 4:02 PM
 */

object DownloadFileFine extends WebCommand with SimpleRequesterGenerator {

  override protected type ArgsTuple      = (String, http.RequestMethod, Map[String, String], String)
  override protected def  primArgsSyntax = Array(StringType, StringType, ListType, StringType)
  override protected def  processArguments(args: Array[Argument]) : ArgsTuple = {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)) getOrElse (throw new ExtensionException("Invalid HTTP method name supplied."))
    val params    =      paramify(args(2)) getOrElse defaultMap
    val filepath  = args(3).getString
    (dest, reqMethod, params, filepath)
  }

  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    val (dest, reqMethod, params, filepath) = processArguments(args)
    val filename = new java.io.File(filepath).getName
    processResponse(generateRequester()(dest, reqMethod, params)) {
      case (response, _) => FileWriter(response, filepath, filename)
    }
  }

}
