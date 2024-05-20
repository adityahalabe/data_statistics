package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}

import javax.inject.Inject
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import services.{ETLService, PersonStatService, TeamStatService}

import scala.language.postfixOps
import scala.concurrent.duration._
import javax.inject._
import play.api.mvc._
import scala.concurrent.ExecutionContext

@Singleton
class PlayerStatController @Inject()(val controllerComponents: ControllerComponents,
                               personStatService: PersonStatService)(implicit exec: ExecutionContext) extends BaseController {

  implicit val system = ActorSystem()
  implicit val  materializer: Materializer = Materializer(system)

  def playerDataStream(lastLoaded: Int): Action[AnyContent] = Action {
    val eventSource: Source[EventSource.Event, NotUsed] = personStatService
      .getPersonSteam
      .drop(lastLoaded)
      .delay(500 milliseconds)
      .map(_.toJson)
      .via(EventSource.flow)

    Ok.chunked(eventSource).as(ContentTypes.EVENT_STREAM)
  }

  def playerStatistics: Action[AnyContent] = Action.async {
    personStatService.getPersonStats.value.map {
      case Left(error) => InternalServerError(error.errorMessage)
      case Right(stats) => Ok(Json.toJson(stats.map(_.toJson)))
    }
  }
}
