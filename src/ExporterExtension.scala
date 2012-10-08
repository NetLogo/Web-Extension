package org.nlogo.extensions.exporter

import java.io.PrintWriter
import java.nio.charset.Charset

import org.nlogo.nvm.ExtensionContext
import org.nlogo.api.{ Argument, Context, DefaultClassManager, DefaultCommand, PrimitiveManager, Syntax }

class ExporterExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export", new Exporter())
  }
}

class Exporter extends DefaultCommand with StreamHandler {
  override def getSyntax = Syntax.commandSyntax(Array(Syntax.StringType | Syntax.OptionalType))
  override def getAgentClassString = "O"
  override def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        val exporter = new RemoteExporter(Option(args(0).getString) getOrElse "") with WISEIntegration
        exporter {(stream: java.io.OutputStream) =>
          val writer = new PrintWriter(stream)
          try     extContext.workspace.exportWorld(writer)
          finally writer.close()
        }
      case _ => throw new IllegalArgumentException("Context is not an `ExtensionContext`!  (How did you even manage to pull that off?)")
    }
  }
}

private class RemoteExporter(dest: String) extends StreamHandler {

  this: WebIntegration =>

  import java.io.ByteArrayOutputStream

  private val ExportKey = "data"

  def apply(hook: (Streamer) => Unit) {
    val exportText  = hookInForText(hook)
    val myPostKVs   = Map(ExportKey -> Option(constructData(exportText)))
    val allPostKVs  = (myPostKVs ++ kvAdditionsMap) collect { case (k, Some(v)) => (k, v) }
    val destOpt     = Option(if (!dest.isEmpty) dest else System.getProperty("netlogo.export_destination"))
    val destination = destOpt getOrElse(throw new IllegalStateException("No valid destination given!"))
    HttpHandler.httpPost(allPostKVs, destination, Option(System.getProperty("wise.cookie")))
  }

  protected def hookInForText(hook: (Streamer) => Unit) : String = {

    val outputStream = new ByteArrayOutputStream()

    try {
      EventEvaluator(outputStream, hook)
      outputStream.toString("UTF-8")
    }
    catch {
      case ex: java.io.UnsupportedEncodingException =>
        System.err.println("Unable to convert hooked text to desired encoding: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
        ""
      case ex: Exception =>
        System.err.println("Unknown error on hooking/exporting: %s\n%s".format(ex.getMessage, ex.getStackTraceString))
        ""
    }
    finally {
      outputStream.close()
    }

  }

}

private object HttpHandler {

  import java.net.URL

  import org.apache.http.{ HttpResponse, client, impl, message }
  import impl.client.DefaultHttpClient
  import message.{ BasicNameValuePair => KVPair }
  import client.entity.{ UrlEncodedFormEntity => URLEntity }, client.methods.HttpPost

  private val DefaultByteEncoding = "UTF-8"

  protected def generateClient = new DefaultHttpClient

  def httpPost(postKVs: Map[String, String], dest: String,
               cookieValue: Option[String] = None, encoding: String = DefaultByteEncoding): String = {

    import collection.JavaConverters.seqAsJavaListConverter

    val client = generateClient

    val post = new HttpPost(new URL(dest).toURI)
    val javaKVs = (postKVs map { case (key, value) => new KVPair(key, value) } toSeq).asJava
    post.setEntity(new URLEntity(javaKVs, Charset.forName(encoding)))

    // Many, many "official" cookie-insertion approaches were tried; all failed --JAB (9/5/12)
    cookieValue foreach (cookie => post.setHeader("COOKIE", "JSESSIONID=" + cookie))

    readResponse(client.execute(post))

  }

  protected def readResponse(response: HttpResponse) =
    response.getStatusLine + ":\n" + io.Source.fromInputStream(response.getEntity.getContent).mkString.trim

}

object EventEvaluator extends StreamHandler {

  import actors.Actor

  sealed protected trait TempActorProtocol
  protected object TempActorProtocol {
    object Start extends TempActorProtocol
  }

  import TempActorProtocol.Start
  
  protected class EventEvaluationActor[T](stream: T, func: (T) => Unit) extends Actor {
    import org.nlogo.swing.Implicits.thunk2runnable
    def act() {
      loop {
        react {
          case Start => org.nlogo.awt.EventQueue.invokeLater{ () => func(stream); reply() }
        }
      }
    }
  }

  def apply(stream: Streamer, hook: (Streamer) => Unit) {
    ((new EventEvaluationActor(stream, hook)).start() !! Start)()
  }

}

sealed trait StreamHandler {
  protected type Streamer = java.io.OutputStream
}

sealed trait WebIntegration {
  protected def kvAdditionsMap                 = Map[String, Option[String]]()
  protected def getProp(prop: String)          = Option(System.getProperty(prop))
  protected def constructData(preData: String) = preData
}

trait WISEIntegration extends WebIntegration {

  private val PeriodIDKey    = "periodId"
  private val RunIDKey       = "runId"
  private val WorkgroupIDKey = "userId"

  private val PeriodIDProp    = "wise.period_id"
  private val RunIDProp       = "wise.run_id"
  private val WorkgroupIDProp = "wise.workgroup_id"

  override protected def constructData(preData: String) = """
                                                            |{"nodeId":"node_0.jn",
                                                            |"visitEndTime":1345775000000,
                                                            |"hintStates":[],
                                                            |"nodeStates":[{"response":"%s"}],
                                                            |"visitStartTime":1345774000000,
                                                            |"nodeType":"JnlpNode",
                                                            |"visitPostTime":null}
                                                          """.format(preData)

  protected override val kvAdditionsMap = Map(
    PeriodIDKey    -> getProp(PeriodIDProp),
    RunIDKey       -> getProp(RunIDProp),
    WorkgroupIDKey -> getProp(WorkgroupIDProp)
  )

}

