package org.nlogo.extensions.web.prim

import
  org.nlogo. { api, app, nvm },
    api.{ Argument, Context, ExtensionException },
    app.App,
    nvm.ExtensionContext

import
  util.{ EnsuranceAgent, Streamer },
    EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/14/13
 * Time: 2:09 PM
 */

object ExportRun extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = ((Streamer) => Unit)
  override protected def  generateRequester = (hook: (Streamer) => Unit) => new StreamerExporter(hook) with Integration with Base64Stream

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = (stream: Streamer) => App.app.tabs.reviewTab.currentRun.fold[Unit](throw new ExtensionException("No run selected."))(_.save(stream))
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

}

