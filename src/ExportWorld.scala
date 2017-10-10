package org.nlogo.extensions.web

import java.io.{ OutputStream, PrintWriter }

import org.nlogo.api.{ Argument, Context, ExtensionException, Reporter, Workspace }
import org.nlogo.core.Syntax.{ ListType, reporterSyntax, StringType }
import org.nlogo.nvm.ExtensionContext

import org.nlogo.extensions.web.requester.{ GZIPStream, SimpleWebIntegration, StreamerExporter }

object ExportWorld extends WebPrimitive with Reporter {

  override def getSyntax = reporterSyntax(right = List(StringType, StringType, ListType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val exporter  = {
      val hook =
        (stream: OutputStream) => {
          val writer = new PrintWriter(stream)
          try context.workspace.exportWorld(writer)
          finally writer.close()
        }
      new StreamerExporter(hook, context.workspace) with SimpleWebIntegration with GZIPStream
    }
    responseToLogoList(exporter(dest, reqMethod, paramMap))
  }

}
