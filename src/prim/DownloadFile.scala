package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context, ExtensionException, Syntax }, Syntax.StringType

import java.io.{ BufferedInputStream, File }
import java.net.URL

import util.FileWriter

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 3:05 PM
 */

object DownloadFile extends WebCommand with WebPrimitive {

  protected type ArgsTuple                  = (String, String)
  protected def  primArgsSyntax: Array[Int] = Array(StringType, StringType)
  protected def  processArguments(args: Array[Argument]) : ArgsTuple = {
    val dest     = args(0).getString
    val filename = args(1).getString
    (dest, filename)
  }

  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    val (dest, filepath) = processArguments(args)
    val filename = dest.reverse takeWhile (_ != '/') reverse
    val bis = new BufferedInputStream(new URL(dest).openStream())
    FileWriter(bis, filepath, filename)
  }

}
