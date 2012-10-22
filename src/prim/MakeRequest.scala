package org.nlogo.extensions.web.prim

import org.nlogo.api.{ Argument, Context, LogoList }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 1:32 PM
 */

object MakeRequest extends WebReporter with CommonWebPrimitive {
  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    ((new Requester with SimpleWebIntegration).apply _).tupled((processArguments(args))) match { case (a, b) => LogoList(isToString(a), b) }
  }
}
