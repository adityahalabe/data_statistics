package model.team

import model.TeamStatAction

case class TeamData(
                       teamId: String,
                       teamName: String,
                       actionId: String,
                       action: TeamStatAction
                      )
