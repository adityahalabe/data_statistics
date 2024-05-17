package model.input


import model.{EventAction, EventGameType, EventPeriod}
import sources.parsers.Parsers.Parser
import sources.parsers.FootballDataEventParser

import java.time.ZonedDateTime

case class FootballDataEvent(actionId: String,
                             competition: String,
                             matchId:String,
                             date:ZonedDateTime,
                             action:EventAction,
                             period:Option[EventPeriod],
                             startTime:Option[String],
                             endtime:Option[String],
                             homeOrAway:Option[EventGameType],
                             teamID:Option[String],
                             teamName:Option[String],
                             personId:Option[String],
                             personName:Option[String],
                             function:Option[String],
                             shirtNr:Option[String],
                             actionReason:Option[String],
                             actionInfo:Option[String],
                             subpersonid:Option[String],
                             subperson:Option[String])

object FootballDataEvent {

  implicit def mapParser : Parser[FootballDataEvent] =
    (dataRow: Map[String,String]) =>FootballDataEventParser.mapToDataEvent(dataRow)
}
