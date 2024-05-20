package model.person

import play.api.libs.json.{JsValue, Json}

case class PersonStats(
                        personId: String,
                        personName: String,
                        yellowCardCount: Int,
                        redCardCount: Int,
                        foulCount: Int,
                        goalCount: Int,
                          ){
  private implicit val implicitPersonStatsWrites = Json.writes[PersonStats]
  def toJson: JsValue = Json.toJson(this)
}

object PersonStats {
  def apply(personId: String, personName: String): PersonStats =
    PersonStats(personId, personName,0,0,0,0)

}