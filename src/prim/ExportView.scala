package org.nlogo.extensions.web.prim

import java.io.{ ByteArrayInputStream, InputStream }

import org.nlogo.api.{ Argument, Context, ExtensionException, Reporter }
import org.nlogo.core.Syntax.{ ListType, reporterSyntax, StringType }
import org.nlogo.nvm.ExtensionContext

import org.nlogo.extensions.web.requester.{ Requester, RequesterGenerator, WebIntegration }
import org.nlogo.extensions.web.util.AsBase64

object ExportView extends WebPrimitive with Reporter with RequesterGenerator {

  override protected type RequesterCons = () => InputStream

  override protected def generateRequester =
    (hook: () => InputStream) =>
      new Requester with Integration {
        override protected def generateAddedExportData = Some(hook())
      }

  override def getSyntax =
    reporterSyntax(right = List(StringType, StringType, ListType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val exporter  = generateRequester { () => new ByteArrayInputStream(AsBase64(context.workspace.exportView).getBytes) }
    responseToLogoList(exporter(dest, reqMethod, paramMap))
  }

}
