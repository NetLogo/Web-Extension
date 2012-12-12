package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context }
import org.nlogo.nvm.ExtensionContext

import util.EnsuranceAgent._
import util.EventEvaluator

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 3:42 PM
 */

// A simpler, more-typical syntax for going an `import-world`, which has no need to use any non-standard library
object ImportWorld extends WebCommand with SimpleWebPrimitive {
  override def perform(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) {
    ensuringExtensionContext { (extContext: ExtensionContext) =>
      val hook = (reader: java.io.InputStreamReader) => {
        extContext.workspace.importWorld(reader)
        reader.close()
      }
      val (dest) = processArguments(args)
      EventEvaluator(io.Source.fromURL(dest).reader(), hook)
    }
  }
}

