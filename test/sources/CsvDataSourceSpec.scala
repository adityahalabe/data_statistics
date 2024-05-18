package sources

import akka.stream.scaladsl.Sink
import util.BaseSpec
import java.nio.file.Paths
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class CsvDataSourceSpec extends BaseSpec{

  "CSV Data Source service" should  {
    "process CSV file" in new BaseFixture {

      val source = csvDataSource.getDataFromPath(file)

      val future: Future[Seq[Map[String, String]]] = source.take(8).runWith(Sink.seq)
      val result = Await.result(future, 3.seconds)

      result.size mustBe 8
      val record = result.find(map => map.get("n_actionid").contains("22024166"))
      record.isDefined mustBe true
      record.get.get("c_person") mustBe Some("Rens Bluemink")
    }
  }
  trait BaseFixture{
    val csvDataSource = new CsvDataSource()
    val file = Paths.get(s"test/data/dataset.csv")
  }
}
