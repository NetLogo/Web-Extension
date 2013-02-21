package org.nlogo.extensions.web.requester

import
  java.io.InputStream

/**
 * Created with IntelliJ IDEA.
 * User: Jason
 * Date: 10/17/12
 * Time: 12:58 PM
 */

sealed trait WebIntegration {
  protected def kvAdditionsMap                      = Map[String, Option[String]]()
  protected def getProp(prop: String)               = Option(System.getProperty(prop))
  protected def constructData(preData: InputStream) = preData
}

trait SimpleWebIntegration extends WebIntegration

//trait WISEIntegration extends WebIntegration {
//
//  private val PeriodIDKey    = "periodId"
//  private val RunIDKey       = "runId"
//  private val WorkgroupIDKey = "userId"
//
//  private val PeriodIDProp    = "wise.period_id"
//  private val RunIDProp       = "wise.run_id"
//  private val WorkgroupIDProp = "wise.workgroup_id"
//
//  override protected def constructData(preData: InputStream) =
//    s"""{"nodeId":"node_0.jn", "visitEndTime":1345775000000, "hintStates":[], "nodeStates":[{"response":"${preData}"}], "visitStartTime":1345774000000, "nodeType":"JnlpNode", "visitPostTime":null}"""
//
//  protected override val kvAdditionsMap = Map(
//    PeriodIDKey    -> getProp(PeriodIDProp),
//    RunIDKey       -> getProp(RunIDProp),
//    WorkgroupIDKey -> getProp(WorkgroupIDProp)
//  )
//
//}

