package db

import akka.stream.scaladsl.Sink
import model.EventAction.{RedCard, YellowCard}
import model.person.{PersonData, PersonStats}
import util.BaseSpec
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class PersonStatDbUpdaterSpec extends BaseSpec{

  "Person Stat Db Updater" should  {
    "update person stats" in new BaseFixture {

      personStatDbUpdater.updatePersonStat(action1).await
      personStatDbUpdater.updatePersonStat(action2).await

      val stats1: Seq[PersonStats] = Await.result(personStatDbUpdater.getAllPersonStats().runWith(Sink.seq), 3.seconds)
      stats1.size mustBe 2
      stats1.find(_.personId == "1").get.yellowCardCount mustBe 1
      personStatDbUpdater.updatePersonStat(action3).await

      personStatDbUpdater.deleteForActionId(action2.actionId).await

      val stats2: Seq[PersonStats] = Await.result(personStatDbUpdater.getAllPersonStats().runWith(Sink.seq), 3.seconds)
      stats2.size mustBe 1
      stats2.find(_.personId == "1").get.yellowCardCount mustBe 2

    }
  }
  trait BaseFixture{
    val personStatDbUpdater = new PersonStatDbUpdater()

    val action1 = PersonData("1", "Player 1","1",YellowCard)
    val action2 = PersonData("2", "Player 2","3",RedCard)
    val action3 = PersonData("1", "Player 1","2",YellowCard)

  }
}
