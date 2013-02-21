package org.nlogo.extensions.web.prim

import
  scala.io.{ Codec, Source }

import
  java.{ io, util },
    io.{ ByteArrayInputStream, InputStream, InputStreamReader },
    util.zip.GZIPInputStream

import
  org.nlogo.{ api, nvm },
    api.{ Argument, Context },
    nvm.ExtensionContext

import
  org.nlogo.extensions.web.{ requester, util => web_util },
    requester.SimpleRequesterGenerator,
    web_util.{ EnsuranceAgent, EventEvaluator },
      EnsuranceAgent._

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 3:42 PM
 */

// A simpler, more-typical syntax for doing an `import-world`
object ImportWorld extends WebCommand with SimpleWebPrimitive with SimpleRequesterGenerator {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = (stream: InputStream) => {
        val gis = new GZIPInputStream(stream)
        extContext.workspace.importWorld(new InputStreamReader(gis))
        stream.close()
      }
      val (dest) = processArguments(args)
      val bytes  = Source.fromURL(dest)(Codec.ISO8859).map(_.toByte).toArray
      val bais   = new ByteArrayInputStream(bytes)
      EventEvaluator(bais, hook)
    }
  }
}

