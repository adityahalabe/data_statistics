package db

import akka.NotUsed
import akka.stream.scaladsl.Source
import model.EventAction._
import model._
import model.team.{TeamData, TeamStats}
import utils.FutureOps.FutureOps
import utils.ResponseT

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TeamStatDbUpdater@Inject()()(implicit exec: ExecutionContext) {

  import TeamStatDbUpdater._

  def updateTeamStat(teamData: TeamData): ResponseT[Unit] = {
    teamsData = teamsData ++ List(teamData)
    Future.successful().toEitherT()
  }

  def getAllTeamStats(): Source[TeamStats, NotUsed] = {
    Source.fromIterator(() => {
      teamsData
        .groupBy(p => (p.teamId,p.teamName))
        .map{
          case (teamDetails, eventsPerTeam) => TeamStats(
            teamDetails._1,
            teamDetails._2,
            eventsPerTeam.count(_.action == Corner),
            eventsPerTeam.count(_.action == OwnGoal),
            eventsPerTeam.count(_.action == PenaltyMissed),
            eventsPerTeam.count(_.action == Goal),
          )
        }.iterator

    })
  }
}

object TeamStatDbUpdater {
  //Temporary Data store
  var teamsData: List[TeamData] = List.empty[TeamData]
}