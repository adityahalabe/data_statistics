package sources

import akka.stream.IOResult
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Source}
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import scala.concurrent.Future

class CsvDataSource {

  private val csvFileFlow = CsvParsing.lineScanner().via(CsvToMap.toMapAsStrings(StandardCharsets.ISO_8859_1))

  def getDataFromPath(file: Path): Source[Map[String, String], Future[IOResult]] = {
    FileIO
      .fromPath(file)
      .via(csvFileFlow)
  }
}
