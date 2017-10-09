package org.nlogo.extensions.web.requester.http

import java.io.{ FileNotFoundException, InputStream }
import java.net.{ MalformedURLException, SocketException, UnknownHostException, URL }

import org.nlogo.api.ExtensionException

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.impl.client.DefaultHttpClient

object RequestSender {

  private val DefaultByteEncoding = "UTF-8"

  protected def generateClient = new DefaultHttpClient

  def apply(dest:        String
          , method:      RequestMethod
          , paramMap:    Map[String, String]
          , lazyMap:     Map[String, InputStream] = Map()
          , cookieValue: Option[String]           = None
          , encoding:    String                   = DefaultByteEncoding
          ): (InputStream, String) = {

    val request = ToApacheConverter(method)

    try request.setURI(new URL(dest).toURI)
    catch {
      case ex: MalformedURLException =>
        throw new ExtensionException(s"${ex.getMessage}\n\nPlease ensure that you have preceded your URL string with the correct protocol (i.e. 'http://').", ex)
    }

    request.handleParams(paramMap, encoding, lazyMap)

    // Many, many "official" cookie-insertion approaches were tried; all failed --JAB (9/5/12)
    cookieValue foreach (cookie => request.setHeader("COOKIE", "JSESSIONID=" + cookie))

    try {
      val (responseStream, statusCode) = prepareResponse(generateClient.execute(request))
      Option(System.getProperty("netlogo.web.debugging")).orElse(Option(System.getProperty("jnlp.netlogo.web.debugging"))).foreach(_ => println(responseStream))
      (responseStream, statusCode)
    } catch {
      case ex: ClientProtocolException =>
        throw new ExtensionException(s"${ex.getMessage}\n\nAn unknown HTTP error has occured.", ex)
      case ex: UnknownHostException =>
        throw new ExtensionException(s"${ex.getMessage}\n\nCould not find the host specified by: $dest", ex)
      case ex: FileNotFoundException =>
        throw new ExtensionException(s"${ex.getMessage}\n\nInvalid URL supplied; please try verify that you have a connection to that resource, " +
                                     "and that the correct URL was supplied for that resource.", ex)
      case ex: SocketException =>
        throw new ExtensionException(s"${ex.getMessage}\n\nRequest failed (likely because the body of was request was too large; " +
                                     "if you haven't already, try using a POST or verifying that " +
                                     "the destination can handle HTTP requests of that size)", ex)
    }

  }

  protected def prepareResponse(response: HttpResponse): (InputStream, String) =
    (response.getEntity.getContent, response.getStatusLine.toString)

}
