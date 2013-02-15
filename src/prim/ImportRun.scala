package org.nlogo.extensions.web.prim

import
  java.net.URL

import
  org.nlogo.{ api, app },
    api.{ Argument, Context },
    app.App

import
  org.apache.commons.codec.binary.Base64InputStream

import
  org.nlogo.extensions.web.util.using

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/15/13
 * Time: 3:20 PM
 */

object ImportRun extends WebCommand with SimpleWebPrimitive {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    val (dest) = processArguments(args)
    using(new URL(dest).openStream()) {
      is => App.app.tabs.reviewTab.loadRun(new Base64InputStream(is))
    }
  }
}
