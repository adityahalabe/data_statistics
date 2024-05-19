package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import db.TeamStatDbUpdater
import model.TeamStatAction
import model.input.FootballDataEvent
import model.team.{TeamData, TeamStats}
import utils.APIException.MandatoryFieldMissing
import utils.FutureOps.FutureOps
import utils.ResponseT

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TeamStatService@Inject()(teamStatDbUpdater:TeamStatDbUpdater)(implicit exec: ExecutionContext) {

  def getData(): Source[TeamStats, NotUsed] = {
    teamStatDbUpdater.getAllTeamStats()
  }
  def updateTeamStat(event:FootballDataEvent): ResponseT[Unit] = {
    import event._
    (action,teamID, teamName) match {
      case (a: TeamStatAction, Some(tId), Some(tName)) =>
        teamStatDbUpdater.updateTeamStat(TeamData(tId, tName, actionId, a))
      case (_: TeamStatAction, None, _) => ResponseT(MandatoryFieldMissing("Team Id"))
      case (_: TeamStatAction, _, None) => ResponseT(MandatoryFieldMissing("Team Name"))
      case _ => Future.successful().toEitherT()
      }
  }

  def deleteForActionId(actionId: String): ResponseT[Unit] =
    teamStatDbUpdater.deleteForActionId(actionId)
}
