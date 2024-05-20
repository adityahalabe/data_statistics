package utils

import java.time.{Duration, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.time._
import scala.util.Try

object DateUtil {

  val excelDateTimeFormat = "dd-MMM-yyyy HH:mm"

  def fromStringDDMMYYYYHHMMSS(stringDate: String): Try[ZonedDateTime] = {
    Try {
      val formatter = DateTimeFormatter.ofPattern(excelDateTimeFormat)
      LocalDateTime.parse(stringDate, formatter).atZone(ZoneId.systemDefault())
    }
  }
  def now: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern(excelDateTimeFormat))
}
