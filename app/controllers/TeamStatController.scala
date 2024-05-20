package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.libs.json.Json
import play.api.mvc._
import services.TeamStatService
import javax.inject._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class TeamStatController @Inject()(val controllerComponents: ControllerComponents,
                                   teamStatService: TeamStatService)(implicit exec: ExecutionContext) extends BaseController {

  implicit val system = ActorSystem()
  implicit val  materializer: Materializer = Materializer(system)

  def teamDataStream(lastLoaded: Int): Action[AnyContent] = Action {
    val eventSource: Source[EventSource.Event, NotUsed] = teamStatService
      .getTeamSteam
      .drop(lastLoaded)
      .delay(500 milliseconds)
      .map(_.toJson)
      .via(EventSource.flow)

    Ok.chunked(eventSource).as(ContentTypes.EVENT_STREAM)
  }

  def teamStatistics: Action[AnyContent] = Action.async {
    teamStatService.getTeamStats.value.map {
      case Left(error) => InternalServerError(error.errorMessage)
      case Right(stats) => Ok(Json.toJson(stats.map(_.toJson)))
    }
  }
}
