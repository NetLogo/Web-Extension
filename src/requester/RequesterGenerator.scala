package org.nlogo.extensions.web.requester

trait RequesterGenerator {
  protected type RequesterCons
  protected type Integration = SimpleWebIntegration
  protected def  generateRequester: (RequesterCons) => Requester with Integration
}

trait SimpleRequesterGenerator extends RequesterGenerator {
  override protected type RequesterCons     = (Unit)
  override protected def  generateRequester = (_: Unit) => new Requester with Integration
}
