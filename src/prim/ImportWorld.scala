package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context, Syntax }, Syntax._
import org.nlogo.nvm.ExtensionContext


/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 3:42 PM
 */

// A simpler, more-typical syntax for going an `import-world`, which has no need to use any non-standard library
object ImportWorld extends WebCommand {

  override protected type ArgsTuple      = (String)
  override protected def  primArgsSyntax = Array(StringType)

  override protected def processArguments(args: Array[Argument]) : ArgsTuple = {
    val dest = args(0).getString
    (dest)
  }

  def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        val (dest) = processArguments(args)
        val reader = io.Source.fromURL(dest).reader()
        extContext.workspace.importWorld(reader)
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }

}

