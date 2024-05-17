package db

import akka.NotUsed
import akka.stream.scaladsl.Source
import model.EventAction._
import model.{team, _}
import model.person.{PersonData, PersonStats}
import utils.FutureOps.FutureOps
import utils.ResponseT

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PersonStatDbUpdater@Inject()()(implicit exec: ExecutionContext) {

  import PersonStatDbUpdater._

  def updatePersonStat(personData: PersonData): ResponseT[Unit] = {
    personsData = personsData ++ List(personData)
    Future.successful().toEitherT()
  }

  def getAllPersonStats(): Source[PersonStats, NotUsed] = {
    Source.fromIterator(() => {
      personsData
        .groupBy(p => (p.personId,p.personName))
        .map{
          case (personDetails, eventsPerPerson) => PersonStats(
            personDetails._1,
            personDetails._2,
            eventsPerPerson.count(_.action == YellowCard),
            eventsPerPerson.count(_.action == RedCard),
            eventsPerPerson.count(_.action == Foul),
            eventsPerPerson.count(_.action == Goal),
          )
        }.iterator

    })
  }
}

object PersonStatDbUpdater {
  //Temporary Data store
  var personsData: List[PersonData] = List.empty[PersonData]
}