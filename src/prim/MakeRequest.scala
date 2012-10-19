package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context, LogoList }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 1:32 PM
 */

object MakeRequest extends WebReporter {
  override def report(args: Array[Argument], context: Context) : AnyRef = {
    ((new Requester with SimpleWebIntegration).apply _).tupled((processArguments(args))) match { case (a, b) => LogoList(a, b) }
  }
}
