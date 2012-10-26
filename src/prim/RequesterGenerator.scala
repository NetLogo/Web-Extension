package org.nlogo.extensions.web.prim

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/26/12
 * Time: 3:35 PM
 */

trait RequesterGenerator {
  protected type RequesterCons
  protected type Integration = SimpleWebIntegration
  protected def  generateRequester: (RequesterCons) => Requester with Integration
}
