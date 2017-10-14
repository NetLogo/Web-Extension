package org.nlogo.extensions.web

import java.io.{ FileInputStream, InputStream }

import org.nlogo.api.{ Argument, Context, ExtensionException, Reporter }
import org.nlogo.core.Syntax.{ ListType, StringType, reporterSyntax }

object UploadFile extends WebPrimitive with Reporter {

  override def getSyntax = reporterSyntax(right = List(StringType, StringType, ListType, StringType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val streamMap = Map("data" -> new FileInputStream(args(3).getString))
    responseToLogoList(mkRequest(dest, reqMethod, paramMap, streamMap))
  }

}
