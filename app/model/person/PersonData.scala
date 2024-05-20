package model.person

import model.PersonStatAction
import play.api.libs.json.{JsValue, Json}

case class PersonData(
                       personId: String,
                       personName: String,
                       actionId: String,
                       action: PersonStatAction
                      ){
  private implicit val implicitPersonDataWrites = Json.writes[PersonData]

  def toJson: JsValue = Json.toJson(this)
}
