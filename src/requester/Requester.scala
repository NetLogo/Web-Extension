package org.nlogo.extensions.web.requester

import java.io.InputStream

import http.{ RequestMethod, RequestSender }

trait Requester {

  self: WebIntegration =>

  private val isWebStart = System.getProperty("javawebstart.version", null) != null

  private val DestinationPropKey = s"${if (isWebStart) "jnlp." else ""}netlogo.export_destination"
  private val CookiePropKey      = s"${if (isWebStart) "jnlp." else ""}netlogo.web.cookie"

  protected def streamMap: Map[String, InputStream] = Map.empty

  def apply(dest: String, httpMethod: RequestMethod, params: Map[String, String]): (InputStream, String) = {
    val streamParams = streamMap.mapValues(constructData)
    val strParams    = params ++ kvAdditionsMap
    val destOpt      = Option(if (!dest.isEmpty) dest else System.getProperty(DestinationPropKey))
    val destination  = destOpt.getOrElse(throw new IllegalStateException("No valid destination given!"))
    RequestSender(destination, httpMethod, strParams, streamParams, Option(System.getProperty(CookiePropKey)))
  }

}
