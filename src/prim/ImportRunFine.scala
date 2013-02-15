package org.nlogo.extensions.web.prim

import
  org.nlogo.{ api, app },
    api.{ Argument, Context },
    app.App

import
  org.apache.commons.codec.binary.Base64InputStream

import
  org.nlogo.extensions.web.requester.SimpleRequesterGenerator

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/15/13
 * Time: 4:47 PM
*/

object ImportRunFine extends WebCommand with CommonWebPrimitive with SimpleRequesterGenerator {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    val (dest, requestMethod, paramMap) = processArguments(args)
    processResponse(generateRequester()(dest, requestMethod, paramMap)) {
      case (response, _) => App.app.tabs.reviewTab.loadRun(new Base64InputStream(response))
    }
  }
}
