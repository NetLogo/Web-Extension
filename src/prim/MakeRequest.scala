package org.nlogo.extensions.web.prim

import org.nlogo.api.{ ExtensionException, Argument, Context, Syntax }

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/19/12
 * Time: 1:32 PM
 */

object MakeRequest extends WebReporter {

  import Syntax._

  // Syntax: <prim> destination http_request_method parameter_map
  override def report(args: Array[Argument], context: Context) : AnyRef = {
    ((new Exporter with SimpleWebIntegration).export _).tupled((processArguments(args)))
  }

}
