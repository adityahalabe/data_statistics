package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import javax.inject.Inject
import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.mvc.ControllerComponents
import services.{ETLService, PersonStatService, TeamStatService}
import scala.language.postfixOps
import scala.concurrent.duration._
import javax.inject._
import play.api.mvc._

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               ETLService: ETLService,
                               personStatService: PersonStatService,
                               teamStatService: TeamStatService) extends BaseController {

  implicit val system = ActorSystem()
  implicit val  materializer: Materializer = Materializer(system)

  def index() = Action.async {
    val data = ETLService.processCsv().runWith(Sink.seq[String])(materializer)
    data.map(d => Ok(views.html.index(d)))(controllerComponents.executionContext)
  }

  def playerDataStream(lastLoaded: Int) = Action {
    val eventSource: Source[EventSource.Event, NotUsed] = personStatService
      .getData()
      .drop(lastLoaded)
      .delay(1 seconds)
      .map(_.toJson)
      .via(EventSource.flow)

    Ok.chunked(eventSource).as(ContentTypes.EVENT_STREAM)
  }
  def teamDataStream(lastLoaded: Int) = Action {
    val eventSource: Source[EventSource.Event, NotUsed] = teamStatService
      .getData()
      .drop(lastLoaded)
      .delay(1 seconds)
      .map(_.toJson)
      .via(EventSource.flow)

    Ok.chunked(eventSource).as(ContentTypes.EVENT_STREAM)
  }
}
