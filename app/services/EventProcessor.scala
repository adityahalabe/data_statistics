package services

import cats.implicits._
import model.input.FootballDataEvent
import sources.parsers.Parsers.Parsable
import utils.{APIException, ResponseT}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EventProcessor @Inject()(personStatService: PersonStatService,
                               teamStatService: TeamStatService)(implicit exec: ExecutionContext) {

  def processEvent(eventDetails: Map[String, String]): ResponseT[String] = {
    (for {
      event <- eventDetails.parseAs[FootballDataEvent].toEitherT[Future]
      _ <- personStatService.updatePersonStat(event)
      _ <- teamStatService.updateTeamStat(event)
    } yield event.actionId).recoverWith {
      case exception: APIException => revertChangesForActionId(exception, eventDetails.get("n_actionid"))
    }
  }

  private def revertChangesForActionId(exception: APIException, actionId: Option[String]): ResponseT[String] = {
    actionId match {
      case Some(aId) => {
        (for {
          _ <- personStatService.deleteForActionId(aId)
          _ <- teamStatService.deleteForActionId(aId)
        } yield ()).flatMap(_ => ResponseT(exception))
      }
      case None => ResponseT(exception)
    }
  }
}
