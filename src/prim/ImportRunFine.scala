package org.nlogo.extensions.web.prim

import
  java.io.InputStream

import
  org.nlogo.{ api, app },
    api.{ Argument, Context },
    app.App

import
  org.apache.commons.codec.binary.Base64InputStream

import
  org.nlogo.extensions.web.{ requester, util },
    requester.SimpleRequesterGenerator,
    util.EventEvaluator

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/15/13
 * Time: 4:47 PM
*/

object ImportRunFine extends WebCommand with CommonWebPrimitive with SimpleRequesterGenerator {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    val hook = (is: InputStream) => {
      val is64 = new Base64InputStream(is)
      App.app.tabs.reviewTab.loadRun(is64)
      is64.close()
    }
    val (dest, requestMethod, paramMap) = processArguments(args)
    processResponse(generateRequester()(dest, requestMethod, paramMap)) {
      case (response, _) => EventEvaluator(response, hook)
    }
  }
}
