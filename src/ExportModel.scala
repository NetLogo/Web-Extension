package org.nlogo.extensions.web

import java.io.{ ByteArrayInputStream, InputStream }

import org.nlogo.api.{ Argument, Context, ExtensionException, Reporter }
import org.nlogo.app.{ App, ModelSaver }
import org.nlogo.core.Syntax.{ ListType, reporterSyntax, StringType }
import org.nlogo.nvm.ExtensionContext

import org.nlogo.fileformat.basicLoader

import org.nlogo.extensions.web.requester.{ Requester, SimpleWebIntegration }

object ExportModel extends WebPrimitive with Reporter {

  override def getSyntax = reporterSyntax(right = List(StringType, StringType, ListType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val exporter  =
      new Requester with SimpleWebIntegration {
        override protected def generateAddedExportData =
          Some {
            val model      = new ModelSaver(App.app, null).currentModelInCurrentVersion
            val modelBytes = basicLoader.sourceString(model, "nlogo").get.getBytes // this may throw an exception if the model couldn't be saved
            new ByteArrayInputStream(modelBytes)
          }
      }
    responseToLogoList(exporter(dest, reqMethod, paramMap))
  }

}
