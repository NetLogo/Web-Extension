package org.nlogo.extensions.exporter

import java.io.PrintWriter

import org.nlogo.nvm.ExtensionContext
import org.nlogo.api.{ Argument, Context, DefaultClassManager, DefaultCommand, PrimitiveManager, Syntax }

//@ Add ability to read a file by passed-in Java property
//@ Add ability to directly read file from a stringed URL
class ExporterExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export-world", new ExportWorld())
  }
}

class ExportWorld extends DefaultCommand with StreamHandler {

  override def getSyntax = Syntax.commandSyntax(Array(Syntax.StringType))
  override def getAgentClassString = "O"
  override def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        val hook = { (stream: java.io.OutputStream) =>
          val writer = new PrintWriter(stream)
          try     extContext.workspace.exportWorld(writer)
          finally writer.close()
        }
        val dest     = Option(args(0).getString) getOrElse ""
        val exporter = new WorldExporter(hook) with WISEIntegration
        exporter.export(dest)
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }

  class WorldExporter(hook: (Streamer) => Unit) extends Exporter {

    self: WebIntegration =>

    import java.io.ByteArrayOutputStream

    private val DefaultByteEncoding = "UTF-8"

    override protected def generateExportStr = {

      val outputStream = new ByteArrayOutputStream()

      try {
        EventEvaluator(outputStream, hook)
        outputStream.toString(DefaultByteEncoding)
      }
      catch {
        case ex: java.io.UnsupportedEncodingException =>
          System.err.println("Unable to convert hooked text to desired encoding: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
          ""
        case ex: Exception =>
          System.err.println("Unknown error on hooking/exporting: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
          ""
      }
      finally {
        outputStream.close()
      }

    }

  }

}


