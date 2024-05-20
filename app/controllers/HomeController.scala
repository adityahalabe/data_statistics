package controllers

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import javax.inject.Inject
import play.api.mvc.ControllerComponents
import services.{ETLService, PersonStatService, TeamStatService}
import scala.language.postfixOps
import javax.inject._
import play.api.mvc._
import utils.DateUtil

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               ETLService: ETLService,
                               personStatService: PersonStatService,
                               teamStatService: TeamStatService) extends BaseController {

  implicit val system = ActorSystem()
  implicit val  materializer: Materializer = Materializer(system)

  def index() = Action {
    Ok(views.html.index())
  }
  def loadData() = Action {
    ETLService.processCsv().runWith(Sink.seq[String])(materializer)
    Ok(s"Data loading process has started at: ${DateUtil.now}")
  }

}
