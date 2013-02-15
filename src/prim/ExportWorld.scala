package org.nlogo.extensions.web.prim

import
  java.io.PrintWriter

import
  org.nlogo.{ api, nvm },
    api.{ Argument, Context },
    nvm.ExtensionContext

import
  org.nlogo.extensions.web.{ requester, util },
    requester.{ ByteStream, RequesterGenerator, StreamerExporter },
    util.{ EnsuranceAgent, Streamer },
      EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/18/12
 * Time: 3:36 PM
 */

// Hooks in and sends an `export-world` to a remote location
object ExportWorld extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = ((Streamer) => Unit)
  override protected def  generateRequester = (hook: (Streamer) => Unit) => new StreamerExporter(hook) with Integration with ByteStream

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { case extContext: ExtensionContext =>
      val hook = {
        (stream: Streamer) =>
          val writer = new PrintWriter(stream)
          try     extContext.workspace.exportWorld(writer)
          finally writer.close()
      }
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

}

