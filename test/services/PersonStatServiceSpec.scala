package services

import akka.stream.scaladsl.Sink
import db.PersonStatDbUpdater
import model.EventAction.{Corner, GenericEventAction, Goal, RedCard, YellowCard}
import model.input.FootballDataEvent
import model.person.PersonData
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import sources.CsvDataSource
import util.BaseSpec
import utils.APIException.MandatoryFieldMissing
import utils.FutureOps.FutureOps

import java.time.ZonedDateTime
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class PersonStatServiceSpec extends BaseSpec{

  "Person Stat Service" should  {
    "update person stats for applicable action" in new BaseFixture {
      import personStatFootballEvent._
      Mockito.when(mockPersonStatDbUpdater.updatePersonStat(PersonData(personId.get,personName.get,actionId,Corner)))
        .thenReturn(Future.successful().toEitherT())
      val updateResult = personStatService.updatePersonStat(personStatFootballEvent).await

      updateResult.isRight mustBe(true)
    }
    "result in error if mandatory fields are missing for person event" in new BaseFixture {
      import personStatFootballEvent._
      Mockito.when(mockPersonStatDbUpdater.updatePersonStat(PersonData(personId.get,personName.get,actionId,Corner)))
        .thenReturn(Future.successful().toEitherT())
      val updateResult = personStatService.updatePersonStat(personStatFootballEvent.copy(personId = None)).await

      updateResult.isLeft mustBe(true)
      updateResult match {
        case Left(m: MandatoryFieldMissing) => m.fieldName mustBe "Person Id"
        case _ => fail("Did not get expected error")
      }
    }
    "return success if action is not applicable for person stats" in new BaseFixture {
      val updateResult = personStatService.updatePersonStat(personStatFootballEvent.copy(action = GenericEventAction("Line up"))).await
      updateResult.isRight mustBe (true)
    }

    "get person statistics aggregate" in new BaseFixture {
      Mockito.when(mockPersonStatDbUpdater.getAllPersonStats()).thenReturn(Future.successful(personStats))
      val result = personStatService.getPersonStats.await
      result.isRight mustBe(true)
      result.map(list => {
        list.find(_.personId == "2").get.goalCount mustBe(2)
        list.find(_.personId == "1").get.redCardCount mustBe(1)
      })
    }
  }

  trait BaseFixture{
    val mockPersonStatDbUpdater = mock[PersonStatDbUpdater]
    val personStatService = new PersonStatService(mockPersonStatDbUpdater)

    val personStatFootballEvent = FootballDataEvent(
      "actionId","competition","matchId",ZonedDateTime.now(),Corner,
      None,None,None,None,None,None,Some("personId"),Some("personName"),None,None,None,None,None,None
    )

    val personStats= List(
      PersonData("1","P 1","action 1",Goal),
      PersonData("1","P 1","action 2",RedCard),
      PersonData("2","P 2","action 3",Goal),
      PersonData("3","P 3","action 4",YellowCard),
      PersonData("2","P 2","action 5",Goal),
    )
  }
}
