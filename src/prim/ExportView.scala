package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context, LogoList }
import org.nlogo.nvm.ExtensionContext

import util.EnsuranceAgent._
import util.ImageToBase64._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 1:17 PM
 */

object ExportView extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = (() => String)
  override protected def  generateRequester = (hook: () => String) => new ViewExporter(hook) with Integration

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = () => extContext.workspace.exportView().asBase64
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook)
      val (response, statusCode) = exporter(dest, requestMethod, paramMap)
      LogoList(isToString(response), statusCode)
    }
  }

  protected class ViewExporter(hook: () => String) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Some(hook())
  }

}
