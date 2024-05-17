package services

import akka.stream.IOResult
import akka.stream.scaladsl.Source

import javax.inject.Inject
import sources.CsvDataSource

import java.nio.file.Paths
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ETLService @Inject() (csvDataSource: CsvDataSource,
                            eventProcessor:EventProcessor){

  def processCsv(): Source[String, Future[IOResult]] = {
    csvDataSource
      .getDataFromPath(Paths.get(s"public/input/dataset.csv"))
      .mapAsync(1){ eventData =>
        eventProcessor
          .processEvent(eventData)
          .value
          .flatMap {
            case Left(value) => Future.failed(value)
            case Right(value) => Future.successful(value)
        }
      }
  }

}