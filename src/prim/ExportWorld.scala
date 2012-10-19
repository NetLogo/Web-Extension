package org.nlogo.extensions.web.prim

import java.io.PrintWriter

import org.nlogo.api.{ExtensionException, Argument, Context, Syntax}
import org.nlogo.nvm.ExtensionContext

import util.{ EventEvaluator, StreamHandler }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 3:36 PM
 */

// Hooks in and sends an `export-world` to a remote location
class ExportWorld extends WebCommand with StreamHandler {

  import Syntax._

  override protected type ArgsTuple  = (String, http.RequestMethod, Map[String, String])
  override protected val  defaultMap = Map[String, String]()

  // Syntax: <prim> destination http_request_method parameter_map
  override def getSyntax = commandSyntax(Array(StringType, StringType, ListType))
  override def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        val hook = {
          (stream: java.io.OutputStream) =>
            val writer = new PrintWriter(stream)
            try     extContext.workspace.exportWorld(writer)
            finally writer.close()
        }
        val (dest, requestMethod, paramMap) = processArguments(args)
        val exporter = new WorldExporter(hook) with WISEIntegration
        exporter.export(dest, requestMethod, paramMap)
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }

  override protected def processArguments(args: Array[Argument]) : ArgsTuple = {
    val dest      = args(0).getString
    val reqMethod = httpMethodify(args(1)) getOrElse (throw new ExtensionException("Invalid HTTP method name supplied."))
    val params    =      paramify(args(2)) getOrElse defaultMap
    (dest, reqMethod, params)
  }

  class WorldExporter(hook: (Streamer) => Unit) extends Exporter {

    self: WebIntegration =>

    import java.io.{ ByteArrayOutputStream, UnsupportedEncodingException }

    private val DefaultByteEncoding = "UTF-8"

    override protected def generateExportStr = {

      val outputStream = new ByteArrayOutputStream()

      try {
        EventEvaluator(outputStream, hook)
        outputStream.toString(DefaultByteEncoding)
      }
      catch {
        case ex: UnsupportedEncodingException =>
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



