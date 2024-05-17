package sources.parsers

import model.EventAction._
import model.EventGameType._
import model.EventPeriod._
import model._
import model.input.FootballDataEvent
import utils.APIException._
import utils.{DateUtil, ParsingException}

import java.time.ZonedDateTime
import scala.util.{Failure, Success}

object FootballDataEventParser {

  def mapToDataEvent(dataRow: Map[String, String]): Either[ParsingException, FootballDataEvent] = {

    for{
      actionId <- getMandatoryField(dataRow,"n_actionid")
      competition <- getMandatoryField(dataRow,"c_competition")
      matchid <- getMandatoryField(dataRow,"n_Matchid")
      date <- parseDate(dataRow)
      action <- getAction(dataRow)
      period <- getPeriod(dataRow)
      homeOrAway <- getEventGameType(dataRow)
    }yield {
      val personid = dataRow.get("n_personid")
      val person = dataRow.get("c_person")
      val StartTime = dataRow.get("n_StartTime")
      val Endtime = dataRow.get("n_Endtime")
      val TeamID = dataRow.get("n_TeamID")
      val Team = dataRow.get("c_Team")
      val Function = dataRow.get("c_Function")
      val ShirtNr = dataRow.get("n_ShirtNr")
      val ActionReason = dataRow.get("c_ActionReason")
      val actionInfo = dataRow.get("c_actionInfo")
      val Subpersonid = dataRow.get("n_Subpersonid")
      val Subperson = dataRow.get("c_Subperson")

      FootballDataEvent(
        actionId,
        competition,
        matchid,
        date,
        action,
        period,
        StartTime,
        Endtime,
        homeOrAway,
        TeamID,
        Team,
        personid,
        person,
        Function,
        ShirtNr,
        ActionReason,
        actionInfo,
        Subpersonid,
        Subperson)
    }
  }

  private def getAction(dataRow: Map[String, String]): Either[ParsingException, EventAction] = {
    dataRow.get("c_Action") match {
      case Some(a) if (a.toLowerCase.contains("red")) => Right(RedCard)
      case Some(a) if (a.toLowerCase == "yellow") => Right(YellowCard)
      case Some(a) if (a.toLowerCase == "goal") => Right(Goal)
      case Some(a) if (a.toLowerCase == "foul committed") => Right(Foul)
      case Some(a) if (a.toLowerCase == "corner") => Right(Corner)
      case Some(a) if (a.toLowerCase == "own goal") => Right(OwnGoal)
      case Some(a) if (a.toLowerCase == "penalty missed") => Right(PenaltyMissed)
      case Some(a)  => Right(GenericEventAction(a))
      case None => Left(MandatoryFieldMissing("c_Action"))
    }
  }

  private def getPeriod(dataRow: Map[String, String]): Either[ParsingException, Option[EventPeriod]] = {
    dataRow.get("c_Period") match {
      case Some(a) if (a.toLowerCase == "start") => Right(Some(Start))
      case Some(a) if (a.toLowerCase == "first half") => Right(Some(FirstHalf))
      case Some(a) if (a.toLowerCase == "second half") => Right(Some(SecondHalf))
      case _ => Right(Option.empty[EventPeriod])
    }
  }
  private def getEventGameType(dataRow: Map[String, String]): Either[ParsingException, Option[EventGameType]] = {
    dataRow.get("c_HomeOrAway") match {
      case Some(a) if (a.toLowerCase == "home") => Right(Some(Home))
      case Some(a) if (a.toLowerCase == "away") => Right(Some(Away))
      case Some(v) if(v.isEmpty)  => Right(Option.empty[EventGameType])
      case Some(_)  => Left(FieldParsingFailed("c_HomeOrAway"))
      case None => Right(Option.empty[EventGameType])
    }
  }

  private def getMandatoryField(dataRow: Map[String, String], key: String):Either[ParsingException, String] = {
    dataRow.get(key) match {
      case Some(a) => Right(a)
      case None => Left(MandatoryFieldMissing(key))
    }
  }

  private def parseDate(dataRow: Map[String, String]): Either[ParsingException, ZonedDateTime] = {
    dataRow.get("d_date") match {
      case Some(a) => DateUtil.fromStringDDMMYYYYHHMMSS(a) match {
        case Failure(_) => Left(FieldParsingFailed("d_date"))
        case Success(value) => Right(value)
      }
      case None => Left(MandatoryFieldMissing("d_date"))
    }
  }
}
