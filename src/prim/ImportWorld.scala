package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.nvm.ExtensionContext

import java.io.StringReader
import util.EventEvaluator

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 2:47 PM
 */

object ImportWorld extends WebCommand {

  def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        val workspace = extContext.workspace()
        val hook = {
          (stream: String) =>
            val reader = new StringReader(stream)
            workspace.importWorld(reader)
        }
        val (dest, requestMethod, paramMap) = processArguments(args)
        (new WorldImporter(hook) with SimpleWebIntegration)(dest, requestMethod, paramMap)
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }

  class WorldImporter(hook: String => Unit) extends Requester {
    self: WebIntegration =>
      override def apply(dest: String, httpMethod: http.RequestMethod, params: Map[String, String]) : (String, String) = {
        val (response, statusCode) = super.apply(dest, httpMethod, params)
        EventEvaluator(response, hook)
        (response, statusCode)
      }
  }

}
