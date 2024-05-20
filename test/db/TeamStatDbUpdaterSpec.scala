package db

import model.EventAction.{OwnGoal, PenaltyMissed}
import model.team.TeamData
import util.BaseSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class TeamStatDbUpdaterSpec extends BaseSpec{

  "Team Stat Db Updater" should  {
    "update team stats" in new BaseFixture {

      teamStatDbUpdater.updateTeamStat(action1).await
      teamStatDbUpdater.updateTeamStat(action2).await

      val stats1: Seq[TeamData] = Await.result(teamStatDbUpdater.getAllTeamStats, 3.seconds)
      stats1.size mustBe 2

      teamStatDbUpdater.deleteForActionId(action2.actionId).await

      val stats2: Seq[TeamData] = Await.result(teamStatDbUpdater.getAllTeamStats, 3.seconds)
      stats2.size mustBe 1

    }
  }
  trait BaseFixture{
    val teamStatDbUpdater = new TeamStatDbUpdater()

    val action1 = TeamData("1", "Player 1","1",OwnGoal)
    val action2 = TeamData("2", "Player 2","3",PenaltyMissed)
    val action3 = TeamData("1", "Player 1","2",OwnGoal)

  }
}
