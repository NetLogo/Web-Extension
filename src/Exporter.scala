package org.nlogo.extensions.web

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:09 PM
 */

trait Exporter {

  self: WebIntegration =>

  private val DestinationPropKey = "netlogo.export_destination"
  private val CookiePropKey      = "netlogo.web.cookie"

  protected def generateExportStr: String

  protected def exportKey = "data"

  def export(dest: String) {
    val exportText  = generateExportStr
    val myPostKVs   = Map(exportKey -> Option(constructData(exportText)))
    val allPostKVs  = (myPostKVs ++ kvAdditionsMap) collect { case (k, Some(v)) => (k, v) }
    val destOpt     = Option(if (!dest.isEmpty) dest else System.getProperty(DestinationPropKey))
    val destination = destOpt getOrElse(throw new IllegalStateException("No valid destination given!"))
    HttpService.post(allPostKVs, destination, Option(System.getProperty(CookiePropKey)))
  }

}
