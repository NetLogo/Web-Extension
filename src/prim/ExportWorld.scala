package org.nlogo.extensions.web.prim

import java.io.{ OutputStream, PrintWriter }

import org.nlogo.api.{ Argument, Context, ExtensionException, Reporter, Workspace }
import org.nlogo.core.Syntax.{ ListType, reporterSyntax, StringType }
import org.nlogo.nvm.ExtensionContext

import org.nlogo.extensions.web.requester.{ GZIPStream, RequesterGenerator, StreamerExporter }

object ExportWorld extends WebPrimitive with Reporter with RequesterGenerator {

  override protected type RequesterCons     = (OutputStream => Unit, Workspace)
  override protected def  generateRequester = (hookAndWS: ((OutputStream => Unit, Workspace))) => new StreamerExporter(hookAndWS._1, hookAndWS._2) with Integration with GZIPStream

  override def getSyntax =
    reporterSyntax(right = List(StringType, StringType, ListType), ret = ListType)

  override def report(args: Array[Argument], context: Context): AnyRef = carefully {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)).getOrElse(throw new ExtensionException("Invalid HTTP method name supplied."))
    val paramMap  = paramify     (args(2)).getOrElse(Map.empty)
    val exporter  = generateRequester({
      (stream: OutputStream) =>
        val writer = new PrintWriter(stream)
        try context.workspace.exportWorld(writer)
        finally writer.close()
    }, context.workspace)
    responseToLogoList(exporter(dest, reqMethod, paramMap))
  }

}
