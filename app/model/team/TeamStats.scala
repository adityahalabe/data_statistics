package model.team

import model.output.Html
import model.output.HtmlTransformers.HtmlTransformer

case class TeamStats(
                        teamId: String,
                        teamName: String,
                        cornerCount: Int,
                        ownGoalCount: Int,
                        penaltyMissedCount: Int,
                        goalCount: Int)

object TeamStats {
  def apply(teamId: String, teamName: String): TeamStats =
    TeamStats(teamId, teamName,0,0,0,0)

  implicit def htmlTransformer: HtmlTransformer[TeamStats] =
    (teamalData: TeamStats) => {
      Html(
        s"""
           |<tr>
           |  <td>${teamalData.teamName}</td>
           |  <td>${teamalData.cornerCount}</td>
           |  <td>${teamalData.ownGoalCount}</td>
           |  <td>${teamalData.penaltyMissedCount}</td>
           |  <td>${teamalData.goalCount}</td>
           |</tr>
       """.stripMargin)
    }
}