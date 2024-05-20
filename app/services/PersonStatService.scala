package services

import akka.NotUsed
import akka.stream.scaladsl.Source
import db.PersonStatDbUpdater
import model.EventAction._
import model.PersonStatAction
import model.input.FootballDataEvent
import model.person.{PersonData, PersonStats}
import utils.APIException.MandatoryFieldMissing
import utils.FutureOps.FutureOps
import utils.ResponseT
import cats.implicits._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PersonStatService@Inject()(personStatDbUpdater:PersonStatDbUpdater)(implicit exec: ExecutionContext) {

  def getPersonStats: ResponseT[List[PersonStats]] = {
    for{
      stats <- personStatDbUpdater.getAllPersonStats().toEitherT()
    }yield {
      stats
        .groupBy(p => (p.personId, p.personName))
        .map {
          case (personDetails, eventsPerPerson) => PersonStats(
            personDetails._1,
            personDetails._2,
            eventsPerPerson.count(_.action == YellowCard),
            eventsPerPerson.count(_.action == RedCard),
            eventsPerPerson.count(_.action == Foul),
            eventsPerPerson.count(_.action == Goal),
          )
        }.toList
    }
  }
  def getPersonSteam: Source[PersonData, NotUsed] = {
    Source.future(
      personStatDbUpdater.getAllPersonStats().map(_.iterator)
    ).flatMapConcat(itr => Source.fromIterator(() => itr))
  }

  def updatePersonStat(event:FootballDataEvent): ResponseT[Unit] = {
    import event._
    (action,personId, personName) match {
        case (a: PersonStatAction,Some(pId),Some(pName)) =>
          personStatDbUpdater.updatePersonStat(PersonData(pId,pName,actionId,a))
        case (_: PersonStatAction,None,_) => ResponseT(MandatoryFieldMissing("Person Id"))
        case (_: PersonStatAction,_,None) => ResponseT(MandatoryFieldMissing("Person Name"))
        case _ => Future.successful(()).toEitherT()
      }
  }

  def deleteForActionId(actionId: String): ResponseT[Unit] =
    personStatDbUpdater.deleteForActionId(actionId)
}
