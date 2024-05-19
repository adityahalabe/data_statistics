package model

sealed trait EventGameType

object EventGameType{
  case object Home extends EventGameType
  case object Away extends EventGameType
}

