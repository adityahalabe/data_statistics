package model.team

import model.TeamStatAction
import play.api.libs.json.{JsValue, Json}

case class TeamData(
                       teamId: String,
                       teamName: String,
                       actionId: String,
                       action: TeamStatAction
                      ){
  private implicit val implicitTeamDataWrites = Json.writes[TeamData]

  def toJson: JsValue = Json.toJson(this)
}
