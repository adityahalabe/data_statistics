package db

import akka.NotUsed
import akka.stream.scaladsl.Source
import model.EventAction._
import model.person.{PersonData, PersonStats}
import utils.FutureOps.FutureOps
import utils.ResponseT
import cats.implicits._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PersonStatDbUpdater@Inject()()(implicit exec: ExecutionContext) {

  import PersonStatDbUpdater._

  def updatePersonStat(personData: PersonData): ResponseT[Unit] = {
    personsData = personsData ++ List(personData)
    Future.successful().toEitherT().map(_ => ())
  }
  def getAllPersonStats(): Future[List[PersonData]] =
    Future.successful(personsData)

  def deleteForActionId(actionId: String): ResponseT[Unit] = {
    personsData = personsData.filterNot(_.actionId == actionId)
    Future.successful().toEitherT().map(_ => ())
  }
}

object PersonStatDbUpdater {
  //Temporary Data store
  var personsData: List[PersonData] = List.empty[PersonData]
}