package services

import model.input.FootballDataEvent
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import util.BaseSpec
import utils.APIException.{FieldParsingFailed, MandatoryFieldMissing, RuntimeError}
import utils.FutureOps.FutureOps
import utils.ResponseT

import scala.concurrent.Future
import scala.language.postfixOps
class EventProcessorSpec extends BaseSpec{

  "Event processor" should  {
    "fail if event is missing Mandatory fields" in new MandatoryFieldsMissingFixture {
      Mockito.when(mockPersonStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      Mockito.when(mockTeamStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      val result = eventProcessor.processEvent(fieldsMissingEvent).await
      result.isLeft mustBe true
      result match {
        case Left(m: MandatoryFieldMissing) => m.fieldName mustBe "n_actionid"
        case _ => fail("Did not get expected error")
      }
    }

    "fail if event parsing fails due non-parsable date" in new InValidDataFixture {
      Mockito.when(mockPersonStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      Mockito.when(mockTeamStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      val result = eventProcessor.processEvent(invalidHDateEvent).await
      result.isLeft mustBe true
      result match {
        case Left(m: FieldParsingFailed) => m.fieldName mustBe "d_date"
        case _ => fail("Did not get expected error")
      }
    }

    "fail if event parsing fails due to non matching data" in new InValidDataFixture {
      Mockito.when(mockPersonStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      Mockito.when(mockTeamStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      val result = eventProcessor.processEvent(invalidHomeOrAwayEvent).await
      result.isLeft mustBe true
      result match {
        case Left(m: FieldParsingFailed) => m.fieldName mustBe "c_HomeOrAway"
        case _ => fail("Did not get expected error")
      }
    }

    "revert the changes if one of event processing fails" in new SuccessFixture {
      Mockito.when(mockPersonStatService.updatePersonStat(any[FootballDataEvent]())).thenReturn(unitResponse)
      Mockito.when(mockTeamStatService.updateTeamStat(any[FootballDataEvent]())).thenReturn(exceptionResponse)
      Mockito.when(mockPersonStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      Mockito.when(mockTeamStatService.deleteForActionId(any[String]())).thenReturn(unitResponse)
      val result = eventProcessor.processEvent(validEvent).await
      result.isLeft mustBe true
    }

    "process event successfully" in new SuccessFixture  {
      Mockito.when(mockPersonStatService.updatePersonStat(any[FootballDataEvent]())).thenReturn(unitResponse)
      Mockito.when(mockTeamStatService.updateTeamStat(any[FootballDataEvent]())).thenReturn(unitResponse)
      val result = eventProcessor.processEvent(validEvent).await
      result.isRight mustBe true
    }
    
  }

  trait BaseFixture{
    val mockPersonStatService = mock[PersonStatService]
    val mockTeamStatService = mock[TeamStatService]
    val eventProcessor = new EventProcessor(mockPersonStatService,mockTeamStatService)
    val unitResponse = Future.successful(()).toEitherT()
    val exceptionResponse: ResponseT[Unit] = ResponseT[Unit](RuntimeError(new RuntimeException("Event saving failed")))
  }

  trait MandatoryFieldsMissingFixture extends BaseFixture {
    val fieldsMissingEvent = Map.empty[String,String]
  }

  trait SuccessFixture extends BaseFixture {
    val validEvent = Map(
      "n_actionid" -> "123124",
      "c_competition" -> "Eredivisie 2017/2018",
      "n_Matchid" -> "2174508",
      "d_date" -> "11-Aug-2017 19:00",
      "c_Action" -> "c_Action",
      "c_HomeOrAway" -> "home",
    )
  }
  trait InValidDataFixture extends SuccessFixture {
    val invalidHDateEvent = validEvent ++ Map("d_date" -> "11/08/2017 19:00")
    val invalidHomeOrAwayEvent = validEvent ++ Map("c_HomeOrAway" -> "XYZZZ")
  }

}
