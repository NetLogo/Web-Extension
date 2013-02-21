package org.nlogo.extensions.web.prim

import
  java.io.{ ByteArrayInputStream, InputStream }

import
  org.nlogo.{ api, app, awt, nvm },
    api.{ Argument, Context },
    app.App,
    awt.Images,
    nvm.{ ExtensionContext, Workspace }

import
  org.nlogo.extensions.web.{ requester, util },
    requester.{ Requester, RequesterGenerator, WebIntegration },
    util.{ EnsuranceAgent, EventEvaluator, ImageToBase64 },
      EnsuranceAgent._,
      ImageToBase64._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 3:05 PM
 */

object ExportInterface extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = ((Unit) => InputStream, Workspace)
  override protected def  generateRequester = (hookAndWS: ((Unit) => InputStream, Workspace)) => (ViewExporter.apply _).tupled(hookAndWS)

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = {
        (_: Unit) =>
          /*
           Yikes!  I didn't want to have to do this; I originally added a method to `Workspace` to
           extract a `BufferedImage` in much the same way that `export-interface` files are written out,
           but, when the view isn't blank (and sometimes when it is), NetLogo will just freeze in some
           Java standard library code when exporting the interface.  Instead, we have to do things in
           such a way here that we ask the workspace to generate this image when it feels up to the task.
           Now that I think of it... why don't we need to do that when exporting the view...? --JAB (10/23/12)
          */
          val component = App.app.tabs.interfaceTab.getInterfacePanel
          new ByteArrayInputStream(Images.paintToImage(component).asBase64.getBytes)
      }
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook, extContext.workspace)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

  protected class ViewExporter(hook: (Unit) => InputStream, workspace: Workspace) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Option(EventEvaluator.withinWorkspace((), hook, workspace))
  }

  // Ladies and gentlemen, I will now lead you in performing one weary "Meh...!" for hacks. --JAB
  // No, seriously, why isn't there better syntactic sugar for partially applying (and tupling) a constructor while mixing in a trait?
  private object ViewExporter {
    def apply(hook: (Unit) => InputStream, workspace: Workspace) = new ViewExporter(hook, workspace) with Integration
  }

}
