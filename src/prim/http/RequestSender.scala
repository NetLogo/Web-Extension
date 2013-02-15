package org.nlogo.extensions.web.prim.http

import
  java.{ io, net },
    io.InputStream,
    net.URL

import
  org.nlogo.api.ExtensionException

import
  org.apache.http.{ HttpResponse, impl },
    impl.client.DefaultHttpClient

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
            cookieValue: Option[String] = None, encoding: String = DefaultByteEncoding) : (InputStream, String) = {

    val request = ToApacheConverter(method)

    try { request.setURI(new URL(dest).toURI) }
    catch {
      case ex: java.net.MalformedURLException =>
        throw new ExtensionException(ex.getMessage + "\n\n" +
                                     "Please ensure that you have preceded your URL string with the correct protocol (i.e. \"http://\").", ex)
    }

    request.handleParams(paramMap, encoding)

    // Many, many "official" cookie-insertion approaches were tried; all failed --JAB (9/5/12)
    cookieValue foreach (cookie => request.setHeader("COOKIE", "JSESSIONID=" + cookie))

    try {
      val (responseStream, statusCode) = prepareResponse(generateClient.execute(request))
      Option(System.getProperty("netlogo.web.debugging")) foreach (_ => println(responseStream))
      (responseStream, statusCode)
    }
    catch {
      case ex: org.apache.http.client.ClientProtocolException =>
        throw new ExtensionException(ex.getMessage + "\n\n" +
                                     "An unknown HTTP error has occured.", ex)
      case ex: java.net.UnknownHostException =>
        throw new ExtensionException(ex.getMessage + "\n\n" +
                                     "Could not find the host specified by: " + dest, ex)
      case ex: java.io.FileNotFoundException =>
        throw new ExtensionException(ex.getMessage + "\n\n" +
                                     "Invalid URL supplied; please try verify that you have a connection to that resource, " +
                                     "and that the correct URL was supplied for that resource.", ex)
      case ex: java.net.SocketException =>
        throw new ExtensionException(ex.getMessage + "\n\n" +
                                     "Request failed (likely because the body of was request was too large; " +
                                     "if you haven't already, try using a POST or verifying that " +
                                     "the destination can handle HTTP requests of that size)", ex)
    }

  }

  protected def prepareResponse(response: HttpResponse) : (InputStream, String) =
    (response.getEntity.getContent, response.getStatusLine.toString)

}

