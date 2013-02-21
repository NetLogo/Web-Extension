package org.nlogo.extensions.web.prim

import
  java.io.{ ByteArrayInputStream, InputStream }

import
  org.nlogo. { api, app, nvm },
    api.{ Argument, Context },
    app.{ App, ModelSaver },
    nvm.ExtensionContext

import
  org.nlogo.extensions.web.{ requester, util },
    requester.{ Requester, RequesterGenerator, WebIntegration },
    util.EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 12/14/12
 * Time: 3:44 PM
 */

object ExportModel extends WebReporter with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = (() => InputStream)
  override protected def  generateRequester = (hook: () => InputStream) => new ModelStringifier(hook) with Integration

  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = () => new ByteArrayInputStream(new ModelSaver(App.app).save.getBytes)
      val (dest, requestMethod, paramMap) = processArguments(args)
      val exporter = generateRequester(hook)
      responseToLogoList(exporter(dest, requestMethod, paramMap))
    }
  }

  protected class ModelStringifier(hook: () => InputStream) extends Requester {
    self: WebIntegration =>
      override protected def generateAddedExportData = Some(hook())
  }

}
