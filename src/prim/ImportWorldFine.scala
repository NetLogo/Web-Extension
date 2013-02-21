package org.nlogo.extensions.web.prim

import
  org.nlogo.{ api, nvm },
    api.{ Argument, Context },
    nvm.ExtensionContext

import
  java.{ io, util },
    io.{ InputStream, InputStreamReader },
    util.zip.GZIPInputStream

import
  org.nlogo.extensions.web.{ requester, util => web_util },
    requester.SimpleRequesterGenerator,
    web_util.{ EnsuranceAgent, EventEvaluator },
      EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 2:47 PM
 */

object ImportWorldFine extends WebCommand with CommonWebPrimitive with SimpleRequesterGenerator {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = (stream: InputStream) => {
        val gis = new GZIPInputStream(stream)
        extContext.workspace.importWorld(new InputStreamReader(gis))
        stream.close()
      }
      val (dest, requestMethod, paramMap) = processArguments(args)
      val (response, _) = generateRequester(hook)(dest, requestMethod, paramMap) // Do not use `processResponse` here; response must be closed in hook
      EventEvaluator(response, hook)
    }
  }
}


