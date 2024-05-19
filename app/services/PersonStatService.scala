package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import db.PersonStatDbUpdater
import model.PersonStatAction
import model.input.FootballDataEvent
import model.person.{PersonData, PersonStats}
import utils.APIException.MandatoryFieldMissing
import utils.FutureOps.FutureOps
import utils.ResponseT

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PersonStatService@Inject()(personStatDbUpdater:PersonStatDbUpdater)(implicit exec: ExecutionContext) {

  def getData(): Source[PersonStats, NotUsed] =
    personStatDbUpdater.getAllPersonStats()

  def updatePersonStat(event:FootballDataEvent): ResponseT[Unit] = {
    import event._
    (action,personId, personName) match {
        case (a: PersonStatAction,Some(pId),Some(pName)) =>
          personStatDbUpdater.updatePersonStat(PersonData(pId,pName,actionId,a))
        case (_: PersonStatAction,None,_) => ResponseT(MandatoryFieldMissing("Person Id"))
        case (_: PersonStatAction,_,None) => ResponseT(MandatoryFieldMissing("Person Name"))
        case _ => Future.successful().toEitherT()
      }
  }

  def deleteForActionId(actionId: String): ResponseT[Unit] =
    personStatDbUpdater.deleteForActionId(actionId)
}
