package org.nlogo.extensions.web.prim

import
  java.{ io, net },
    io.InputStream,
    net.URL

import
  org.nlogo.{ api, app },
    api.{ Argument, Context },
    app.App

import
  org.apache.commons.codec.binary.Base64InputStream

import
  org.nlogo.extensions.web.util.{ EventEvaluator, using }

/**
 * Created with IntelliJ IDEA.
 * User: jason
 * Date: 2/15/13
 * Time: 3:20 PM
 */

object ImportRun extends WebCommand with SimpleWebPrimitive {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    val hook = (is: InputStream) => {
      val is64 = new Base64InputStream(is)
      App.app.tabs.reviewTab.loadRun(is64)
      is64.close()
    }
    val (dest) = processArguments(args)
    using(new URL(dest).openStream()) {
      is => EventEvaluator(is, hook)
    }
  }
}
