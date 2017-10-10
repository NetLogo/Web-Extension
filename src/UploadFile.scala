package org.nlogo.extensions.web

import java.io.{ FileInputStream, InputStream }

import org.nlogo.api.{ Argument, Context, ExtensionException, Reporter }
import org.nlogo.core.Syntax.{ ListType, StringType, reporterSyntax }
import org.nlogo.extensions.web.requester.{ Requester, SimpleWebIntegration }
import org.nlogo.extensions.web.requester.http.RequestMethod

object UploadFile extends WebPrimitive with Reporter {

  override def getSyntax = reporterSyntax(right = List(StringType, StringType, ListType, StringType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val filePath  = args(3).getString
    val exporter  =
      new Requester with SimpleWebIntegration {
        override protected def generateAddedExportData = Some(new FileInputStream(filePath))
      }
    responseToLogoList(exporter(dest, reqMethod, paramMap))
  }

}
