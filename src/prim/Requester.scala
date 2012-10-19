package org.nlogo.extensions.web.prim

import java.io.InputStream

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:09 PM
 */

trait Requester {

  self: WebIntegration =>

  private val DestinationPropKey = "netlogo.export_destination"
  private val CookiePropKey      = "netlogo.web.cookie"

  protected def generateAddedExportData: Option[String] = None

  protected def exportKey = "data"

  def apply(dest: String, httpMethod: http.RequestMethod, params: Map[String, String]) : (InputStream, String) = {
    val myPostKVs   = Map() ++ (generateAddedExportData map (str => Map(exportKey -> Option(constructData(str)))) getOrElse Map())
    val allPostKVs  = params ++ ((myPostKVs ++ kvAdditionsMap) collect { case (k, Some(v)) => (k, v) })
    val destOpt     = Option(if (!dest.isEmpty) dest else System.getProperty(DestinationPropKey))
    val destination = destOpt getOrElse(throw new IllegalStateException("No valid destination given!"))
    http.RequestSender(destination, httpMethod, allPostKVs, Option(System.getProperty(CookiePropKey)))
  }

}
