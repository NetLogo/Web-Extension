package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.app.App
import org.nlogo.awt.Images
import org.nlogo.nvm.{ ExtensionContext, Workspace }

import util.EnsuranceAgent._
import util.EventEvaluator
import util.ImageToBase64._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 3:05 PM
 */

object ExportInterface extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = ((Unit) => String, Workspace)
  override protected def  generateRequester = (hookAndWS: ((Unit) => String, Workspace)) => (ViewExporter.apply _).tupled(hookAndWS)

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
          Images.paintToImage(component).asBase64
      }
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook, extContext.workspace)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

  protected class ViewExporter(hook: (Unit) => String, workspace: Workspace) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Option(EventEvaluator.withinWorkspace((), hook, workspace))
  }

  // Ladies and gentlemen, I will now lead you in performing one weary "Meh...!" for hacks. --JAB
  // No, seriously, why isn't there better syntactic sugar for partially applying (and tupling) a constructor while mixing in a trait?
  private object ViewExporter {
    def apply(hook: (Unit) => String, workspace: Workspace) = new ViewExporter(hook, workspace) with Integration
  }

}
