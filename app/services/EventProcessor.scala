package services

import model.input.FootballDataEvent
import sources.parsers.Parsers.Parsable
import cats.implicits._
import utils.ResponseT

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EventProcessor@Inject()(personStatService:PersonStatService,teamStatService: TeamStatService)(implicit exec: ExecutionContext) {

  def processEvent(eventDetails: Map[String,String]): ResponseT[String] = {
    for {
      event <- eventDetails.parseAs[FootballDataEvent].toEitherT[Future]
      _ <- personStatService.updatePersonStat(event)
      _ <- teamStatService.updateTeamStat(event)
    } yield event.actionId
  }

}
