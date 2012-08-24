package org.nlogo.extensions.exporter

import java.io.PrintWriter
import java.nio.charset.Charset

import org.nlogo.nvm.ExtensionContext
import org.nlogo.api.{Argument, Context, DefaultClassManager, DefaultCommand, PrimitiveManager, Syntax}

class ExporterExtension extends DefaultClassManager {
  def load(primitiveManager: PrimitiveManager) {
    primitiveManager.addPrimitive("export", new Exporter())
  }
}

class Exporter extends DefaultCommand with StreamHandler {
  override def getSyntax = Syntax.commandSyntax(Array(Syntax.StringType))
  override def getAgentClassString = "O"
  override def perform(args: Array[Argument], context: Context) {
    context match {
      case extContext: ExtensionContext =>
        val exporter = new RemoteExporter(args(0).getString) with WISEIntegration
        exporter{(stream: java.io.OutputStream) =>
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
    val exportText = hookInForText(hook)
    val myPostKVs  = Map(ExportKey -> Option(exportText))
    val allPostKVs = (myPostKVs ++ kvAdditionsMap) collect { case (k, Some(v)) => (k, v) }
    HttpHandler.httpPost(allPostKVs, dest)
  }

  protected def hookInForText(hook: (Streamer) => Unit) : String = {

    val outputStream = new ByteArrayOutputStream()
    EventEvaluator(outputStream, hook)

    try     { outputStream.toString("UTF-8") }
    catch   { case ex => System.err.println("An error has occurred in hooking/exporting: " + ex.getMessage); "" }
    finally { outputStream.close() }

  }

}

private[exporter] object HttpHandler {

  import org.apache.http.{HttpResponse, client}, client.entity.{UrlEncodedFormEntity => URLEntity}, client.methods.HttpPost
  import org.apache.http.message.{BasicNameValuePair => KVPair}

  private val DefaultByteEncoding = "ISO-8859-1"
  private val DestProp            = "netlogo.export_destination"

  protected def httpClient = new org.apache.http.impl.client.DefaultHttpClient

  def httpPost(postKVs: Map[String, String], dest: String, encoding: String = DefaultByteEncoding): String = {
    import collection.JavaConverters.seqAsJavaListConverter
    val destination = Option(if (!dest.isEmpty) dest else System.getProperty(DestProp)) getOrElse (throw new IllegalStateException("No valid destination given!"))
    val post = new HttpPost(new java.net.URL(destination).toURI)
    post.setEntity(new URLEntity((postKVs map { case (key, value) => new KVPair(key, value) } toSeq) asJava, Charset.forName("UTF-8")))
    readResponse(httpClient.execute(post))
  }

  protected def readResponse(response: HttpResponse) = io.Source.fromInputStream(response.getEntity.getContent).mkString.trim

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
  protected def kvAdditionsMap        = Map[String, Option[String]]()
  protected def getProp(prop: String) = Option(System.getProperty(prop))
}

trait WISEIntegration extends WebIntegration {

  private val PeriodIDKey    = "periodId"
  private val RunIDKey       = "runId"
  private val WorkgroupIDKey = "workgroupId"

  private val PeriodIDProp    = "wise.period_id"
  private val RunIDProp       = "wise.run_id"
  private val WorkgroupIDProp = "wise.workgroup_id"

  protected override val kvAdditionsMap = Map(
    PeriodIDKey -> getProp(PeriodIDProp),
    RunIDKey    -> getProp(RunIDProp),
    WorkgroupIDKey   -> getProp(WorkgroupIDProp)
  )

}

