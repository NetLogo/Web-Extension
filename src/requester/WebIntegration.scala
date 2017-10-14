package org.nlogo.extensions.web.requester

import java.io.InputStream

sealed trait WebIntegration {
  protected def kvAdditionsMap                      = Map.empty[String, String]
  protected def getProp(prop: String)               = Option(System.getProperty(prop))
  protected def constructData(preData: InputStream) = preData
}

trait SimpleWebIntegration extends WebIntegration
