package model

sealed trait EventPeriod

object EventPeriod{
  case object Start extends EventPeriod
  case object FirstHalf extends EventPeriod
  case object HalfTime extends EventPeriod
  case object SecondHalf extends EventPeriod
}

