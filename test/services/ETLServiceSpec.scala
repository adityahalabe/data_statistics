package services

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import org.mockito.ArgumentMatchers.any
import org.mockito.{Mockito, MockitoSugar}
import org.scalatestplus.play.PlaySpec
import sources.CsvDataSource
import util.BaseSpec
import utils.FutureOps.FutureOps

import java.nio.file.{Path, Paths}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

class ETLServiceSpec extends BaseSpec{

  "ETL service" should  {
    "process CSV file" in new BaseFixture {

      Mockito.when(mockCsvDataSource.getDataFromPath(any[Path]())).thenReturn(sampleData)
      Mockito.when(mockEventProcessor.processEvent(any[Map[String,String]]()))
        .thenReturn(Future.successful("actionId").toEitherT())

      val source = ETLService.processCsv()
      val future = source.take(10).runWith(Sink.seq)
      val result = Await.result(future, 3.seconds)

      result.head mustBe "Event Processed with Action ID :actionId"
    }
  }

  trait BaseFixture{
    val mockCsvDataSource = mock[CsvDataSource]
    val mockEventProcessor = mock[EventProcessor]
    val ETLService = new ETLService(csvDataSource = mockCsvDataSource, eventProcessor = mockEventProcessor)
    val file = Paths.get(s"test/data/dataset.csv")
    val sampleData = new CsvDataSource().getDataFromPath(file)
  }
}
