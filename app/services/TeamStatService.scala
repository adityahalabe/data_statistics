package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import db.TeamStatDbUpdater
import model.EventAction._
import model.TeamStatAction
import model.input.FootballDataEvent
import model.team.{TeamData, TeamStats}
import utils.APIException.MandatoryFieldMissing
import utils.FutureOps.FutureOps
import utils.ResponseT
import cats.implicits._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TeamStatService@Inject()(teamStatDbUpdater:TeamStatDbUpdater)(implicit exec: ExecutionContext) {

  def getTeamStats: ResponseT[List[TeamStats]] = {
    for {
      stats <- teamStatDbUpdater.getAllTeamStats.toEitherT()
    } yield {
      stats
        .groupBy(p => (p.teamId, p.teamName))
        .map {
          case (teamDetails, eventsPerTeam) => TeamStats(
            teamDetails._1,
            teamDetails._2,
            eventsPerTeam.count(_.action == Corner),
            eventsPerTeam.count(_.action == OwnGoal),
            eventsPerTeam.count(_.action == PenaltyMissed),
            eventsPerTeam.count(_.action == Goal),
          )
        }.toList
    }
  }

  def getTeamSteam: Source[TeamData, NotUsed] = {
    Source.future(
      teamStatDbUpdater.getAllTeamStats.map(_.iterator)
    ).flatMapConcat(itr => Source.fromIterator(() => itr))
  }

  def updateTeamStat(event:FootballDataEvent): ResponseT[Unit] = {
    import event._
    (action,teamId, teamName) match {
      case (a: TeamStatAction, Some(tId), Some(tName)) =>
        teamStatDbUpdater.updateTeamStat(TeamData(tId, tName, actionId, a))
      case (_: TeamStatAction, None, _) => ResponseT(MandatoryFieldMissing("Team Id"))
      case (_: TeamStatAction, _, None) => ResponseT(MandatoryFieldMissing("Team Name"))
      case _ => Future.successful(()).toEitherT()
      }
  }

  def deleteForActionId(actionId: String): ResponseT[Unit] =
    teamStatDbUpdater.deleteForActionId(actionId)
}
