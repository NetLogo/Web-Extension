package org.nlogo.extensions.web.prim

import
  java.io.{ ByteArrayInputStream, InputStream }

import
  org.nlogo. { api, app, nvm },
    api.{ Argument, Context },
    app.{ ModelSaver },  //App, 
    nvm.ExtensionContext

import org.nlogo.fileformat.basicLoader

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
	  val hook = { () =>
	  	val model = new ByteArrayInputStream(new ModelSaver(App.app, null).currentModelInCurrentVersion
	  	val modelBytes = basicLoader.sourceString(model, "nlogo").get.getBytes // this may throw an exception if the model couldn't be saved
	  	new ByteArrayInputStream(modelBytes)
	  }
	  //val hook = () => new ByteArrayInputStream(new ModelSaver(App.app).save.getBytes)  //replaced by lines above
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
