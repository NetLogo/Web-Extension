package org.nlogo.extensions.web.prim

import
  java.io.{ ByteArrayInputStream, InputStream }

import
  org.nlogo.{ api, nvm },
    api.{ Argument, Context },
    nvm.ExtensionContext

import
  org.nlogo.extensions.web.{ requester, util },
    requester.{ Requester, RequesterGenerator, WebIntegration },
    util.{ EnsuranceAgent, ImageToBase64 },
      EnsuranceAgent._,
      ImageToBase64._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/22/12
 * Time: 1:17 PM
 */

object ExportView extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = (() => InputStream)
  override protected def  generateRequester = (hook: () => InputStream) => new ViewExporter(hook) with Integration

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = () => new ByteArrayInputStream(extContext.workspace.exportView().asBase64.getBytes)
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

  protected class ViewExporter(hook: () => InputStream) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Some(hook())
  }

}
