package services

import db.TeamStatDbUpdater
import model.EventAction.{Corner, GenericEventAction, Goal, OwnGoal, PenaltyMissed, RedCard, YellowCard}
import model.input.FootballDataEvent
import model.team.TeamData
import org.mockito.Mockito
import util.BaseSpec
import utils.APIException.MandatoryFieldMissing
import utils.FutureOps.FutureOps

import java.time.ZonedDateTime
import scala.concurrent.Future
import scala.language.postfixOps

class TeamStatServiceSpec extends BaseSpec{

  "Team Stat Service" should  {
    "update team stats for applicable action" in new BaseFixture {
      import teamStatFootballEvent._
      Mockito.when(mockTeamStatDbUpdater.updateTeamStat(TeamData(teamId.get,teamName.get,actionId,Corner)))
        .thenReturn(Future.successful().toEitherT())
      val updateResult = teamStatService.updateTeamStat(teamStatFootballEvent).await

      updateResult.isRight mustBe(true)
    }
    "result in error if mandatory fields are missing for team event" in new BaseFixture {
      import teamStatFootballEvent._
      Mockito.when(mockTeamStatDbUpdater.updateTeamStat(TeamData(teamId.get,teamName.get,actionId,Corner)))
        .thenReturn(Future.successful().toEitherT())
      val updateResult = teamStatService.updateTeamStat(teamStatFootballEvent.copy(teamId = None)).await

      updateResult.isLeft mustBe(true)
      updateResult match {
        case Left(m: MandatoryFieldMissing) => m.fieldName mustBe "Team Id"
        case _ => fail("Did not get expected error")
      }
    }
    "return success if action is not applicable for team stats" in new BaseFixture {
      val updateResult = teamStatService.updateTeamStat(teamStatFootballEvent.copy(action = GenericEventAction("Line up"))).await
      updateResult.isRight mustBe (true)
    }

    "get team statistics aggregate" in new BaseFixture {
      Mockito.when(mockTeamStatDbUpdater.getAllTeamStats).thenReturn(Future.successful(teamStats))
      val result = teamStatService.getTeamStats.await
      result.isRight mustBe(true)
      result.map(list => {
        list.find(_.teamId == "2").get.goalCount mustBe(2)
        list.find(_.teamId == "1").get.penaltyMissedCount mustBe(1)
      })
    }
  }

  trait BaseFixture{
    val mockTeamStatDbUpdater = mock[TeamStatDbUpdater]
    val teamStatService = new TeamStatService(mockTeamStatDbUpdater)

    val teamStatFootballEvent = FootballDataEvent(
      "actionId","competition","matchId",ZonedDateTime.now(),Corner,
      None,None,None,None,Some("teamId"),Some("teamName"),None,None,None,None,None,None,None,None
    )

    val teamStats= List(
      TeamData("1","P 1","action 1",Goal),
      TeamData("1","P 1","action 2",PenaltyMissed),
      TeamData("2","P 2","action 3",Goal),
      TeamData("3","P 3","action 4",OwnGoal),
      TeamData("2","P 2","action 5",Goal),
    )
  }
}
