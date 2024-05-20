package model

import play.api.libs.json.{Json, Writes}

sealed trait EventAction

sealed trait PersonStatAction extends EventAction

sealed trait TeamStatAction extends EventAction

object EventAction {
  case object RedCard extends PersonStatAction

  case object YellowCard extends PersonStatAction

  case object Foul extends PersonStatAction

  case class GenericEventAction(action: String) extends EventAction

  case object Goal extends PersonStatAction with TeamStatAction

  case object Corner extends PersonStatAction with TeamStatAction

  case object OwnGoal extends PersonStatAction with TeamStatAction

  case object PenaltyMissed extends PersonStatAction with TeamStatAction

  implicit object PersonStatActionWrites extends Writes[PersonStatAction] {
    def writes(action: PersonStatAction) = action match {
      case a: PersonStatAction => Json.toJson(a.toString)
    }
  }
  implicit object TeamStatActionWrites extends Writes[TeamStatAction] {
    def writes(action: TeamStatAction) = action match {
      case a: TeamStatAction => Json.toJson(a.toString)
    }
  }
}