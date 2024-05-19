package db

import akka.stream.scaladsl.Sink
import model.EventAction.{OwnGoal, PenaltyMissed}
import model.team.{TeamData, TeamStats}
import util.BaseSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class TeamStatDbUpdaterSpec extends BaseSpec{

  "Team Stat Db Updater" should  {
    "update team stats" in new BaseFixture {

      teamStatDbUpdater.updateTeamStat(action1).await
      teamStatDbUpdater.updateTeamStat(action2).await

      val stats1: Seq[TeamStats] = Await.result(teamStatDbUpdater.getAllTeamStats().runWith(Sink.seq), 3.seconds)
      stats1.size mustBe 2
      stats1.find(_.teamId == "1").get.ownGoalCount mustBe 1
      teamStatDbUpdater.updateTeamStat(action3).await

      teamStatDbUpdater.deleteForActionId(action2.actionId).await

      val stats2: Seq[TeamStats] = Await.result(teamStatDbUpdater.getAllTeamStats().runWith(Sink.seq), 3.seconds)
      stats2.size mustBe 1
      stats2.find(_.teamId == "1").get.ownGoalCount mustBe 2

    }
  }
  trait BaseFixture{
    val teamStatDbUpdater = new TeamStatDbUpdater()

    val action1 = TeamData("1", "Player 1","1",OwnGoal)
    val action2 = TeamData("2", "Player 2","3",PenaltyMissed)
    val action3 = TeamData("1", "Player 1","2",OwnGoal)

  }
}
