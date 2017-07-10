package org.nlogo.extensions.web.prim

import
  org.nlogo.api.{ Argument, Context }

import
  org.nlogo.extensions.web.{ requester },      //, util },
    requester.{ Requester, RequesterGenerator }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 1:32 PM
 */

object MakeRequest extends WebReporter with CommonWebPrimitive with RequesterGenerator {
  override protected type RequesterCons     = (Unit)
  override protected def  generateRequester = (_: Unit) => new Requester with Integration
  override def report(args: Array[Argument])(implicit context: Context, ignore: DummyImplicit) : AnyRef = {
    (generateRequester(()).apply _).tupled((processArguments(args))) match { case x => responseToLogoList(x) }
  }
}
