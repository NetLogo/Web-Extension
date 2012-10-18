package org.nlogo.extensions.web.prim.http

import java.net.URL

import org.apache.http.{ HttpResponse, impl }, impl.client.DefaultHttpClient

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 1:00 PM
 */

object RequestSender {

  private val DefaultByteEncoding = "UTF-8"

  protected def generateClient = new DefaultHttpClient

  def apply(dest: String, method: RequestMethod, paramMap: Map[String, String],
            cookieValue: Option[String] = None, encoding: String = DefaultByteEncoding) : String = {

    val request = ToApacheConverter(method)
    request.setURI(new URL(dest).toURI)
    request.handleParams(paramMap, encoding)

    // Many, many "official" cookie-insertion approaches were tried; all failed --JAB (9/5/12)
    cookieValue foreach (cookie => request.setHeader("COOKIE", "JSESSIONID=" + cookie))

    try {
      val response = readResponse(generateClient.execute(request))
      Option(System.getProperty("netlogo.web.debugging")) foreach (_ => println(response))
      response

    }
    catch {
      case ex: java.net.SocketException =>
        throw new org.nlogo.api.ExtensionException(ex.getMessage + "\n\n" +
                                                   "Request failed (likely because the body of was request was too large; " +
                                                   "if you haven't already, try using a POST or verifying that " +
                                                   "the destination can handle HTTP requests of that size)", ex)
    }

  }

  protected def readResponse(response: HttpResponse) =
    response.getStatusLine + ":\n" + io.Source.fromInputStream(response.getEntity.getContent).mkString.trim

}

