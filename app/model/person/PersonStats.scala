package model.person

import model.output.Html
import model.output.HtmlTransformers.HtmlTransformer

case class PersonStats(
                        personId: String,
                        personName: String,
                        yellowCardCount: Int,
                        redCardCount: Int,
                        foulCount: Int,
                        goalCount: Int,
                          )

object PersonStats {
  def apply(personId: String, personName: String): PersonStats =
    PersonStats(personId, personName,0,0,0,0)

  implicit def htmlTransformer: HtmlTransformer[PersonStats] =
    (personalData: PersonStats) => {
      Html(
        s"""
           |<tr>
           |  <td>${personalData.personName}</td>
           |  <td>${personalData.yellowCardCount}</td>
           |  <td>${personalData.redCardCount}</td>
           |  <td>${personalData.foulCount}</td>
           |  <td>${personalData.goalCount}</td>
           |</tr>
       """.stripMargin)
    }
}