package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.nvm.ExtensionContext

import java.io.{ InputStream, InputStreamReader }

import util.EnsuranceAgent._
import util.EventEvaluator

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 2:47 PM
 */

object ImportWorldFine extends WebCommand with CommonWebPrimitive with RequesterGenerator {

  override protected type RequesterCons     = (InputStream => Unit)
  override protected def  generateRequester = (hook: InputStream => Unit) => new WorldImporter(hook) with Integration

  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val workspace = extContext.workspace()
      val hook = (stream: InputStream) => workspace.importWorld(new InputStreamReader(stream))
      val (dest, requestMethod, paramMap) = processArguments(args)
      generateRequester(hook)(dest, requestMethod, paramMap)
    }
  }

  protected class WorldImporter(hook: InputStream => Unit) extends Requester {
    self: WebIntegration =>
    override def apply(dest: String, httpMethod: http.RequestMethod, params: Map[String, String]) : (InputStream, String) = {
      val (responseStream, statusCode) = super.apply(dest, httpMethod, params)
      EventEvaluator(responseStream, hook)
      (responseStream, statusCode)
    }
  }

}


