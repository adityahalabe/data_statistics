package model

sealed trait EventAction

sealed trait PersonStatAction extends EventAction

sealed trait TeamStatAction extends EventAction

object EventAction{
  case object RedCard extends PersonStatAction

  case object YellowCard extends PersonStatAction

  case object Foul extends PersonStatAction

  case class GenericEventAction(action: String) extends EventAction

  case object Goal extends PersonStatAction with TeamStatAction

  case object Corner extends PersonStatAction with TeamStatAction

  case object OwnGoal extends PersonStatAction with TeamStatAction

  case object PenaltyMissed extends PersonStatAction with TeamStatAction
}