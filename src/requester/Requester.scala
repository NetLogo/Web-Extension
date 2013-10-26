package org.nlogo.extensions.web.requester

import
  java.io.InputStream

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:09 PM
 */

trait Requester {

  self: WebIntegration =>

  private val isWebStart = System.getProperty("javawebstart.version", null) != null

  private val DestinationPropKey = (if (isWebStart) "jnlp." else "") + "netlogo.export_destination"
  private val CookiePropKey      = (if (isWebStart) "jnlp." else "") + "netlogo.web.cookie"

  protected def generateAddedExportData: Option[InputStream] = None

  protected def exportKey = "data"

  def apply(dest: String, httpMethod: http.RequestMethod, params: Map[String, String]) : (InputStream, String) = {
    val rawParams   = sink(Map() ++ (generateAddedExportData map (is => Map(exportKey -> Option(constructData(is)))) getOrElse Map()))
    val strParams   = params ++ sink(kvAdditionsMap)
    val destOpt     = Option(if (!dest.isEmpty) dest else System.getProperty(DestinationPropKey))
    val destination = destOpt getOrElse(throw new IllegalStateException("No valid destination given!"))
    http.RequestSender(destination, httpMethod, strParams, rawParams, Option(System.getProperty(CookiePropKey)))
  }

  private def sink[T, U](map: Map[T, Option[U]]) : Map[T, U] =
    map collect { case (k, Some(v)) => (k, v) }

}
