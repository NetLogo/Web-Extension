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

object ExportView extends WebReporter with CommonWebPrimitive {

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = () => extContext.workspace.exportView().asBase64
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = new ViewExporter(hook) with SimpleWebIntegration
      val (response, statusCode) = exporter(dest, requestMethod, paramMap)
      LogoList(isToString(response), statusCode)
    }
  }

  private class ViewExporter(hook: () => String) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Some(hook())
  }

}
