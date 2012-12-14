package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.nvm.ExtensionContext

import util.EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 12/14/12
 * Time: 3:44 PM
 */

object ExportModel extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = (() => String)
  override protected def  generateRequester = (hook: () => String) => new ModelStringifier(hook) with Integration

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = () => new org.nlogo.app.ModelSaver(org.nlogo.app.App.app).save
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

  protected class ModelStringifier(hook: () => String) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Some(hook())
  }

}
