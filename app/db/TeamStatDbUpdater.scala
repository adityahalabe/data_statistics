package db

import cats.implicits._
import model.team.TeamData
import utils.FutureOps.FutureOps
import utils.ResponseT

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TeamStatDbUpdater@Inject()()(implicit exec: ExecutionContext) {

  import TeamStatDbUpdater._

  def updateTeamStat(teamData: TeamData): ResponseT[Unit] = {
    teamsData = teamsData ++ List(teamData)
    Future.successful().toEitherT().map(_ => ())
  }

  def getAllTeamStats: Future[List[TeamData]] = {
    Future.successful(teamsData)
  }
  def deleteForActionId(actionId: String): ResponseT[Unit] = {
    teamsData = teamsData.filterNot(_.actionId == actionId)
    Future.successful().toEitherT().map(_ => ())
  }
}

object TeamStatDbUpdater {
  //Temporary Data store
  var teamsData: List[TeamData] = List.empty[TeamData]
}