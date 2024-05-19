package model.team

import play.api.libs.json.{JsValue, Json}

case class TeamStats(
                        teamId: String,
                        teamName: String,
                        cornerCount: Int,
                        ownGoalCount: Int,
                        penaltyMissedCount: Int,
                        goalCount: Int){
  private implicit val implicitTeamStatsWrites = Json.writes[TeamStats]
  def toJson: JsValue = Json.toJson(this)
}

object TeamStats {
  def apply(teamId: String, teamName: String): TeamStats =
    TeamStats(teamId, teamName,0,0,0,0)

}