package model.person

import model.PersonStatAction

case class PersonData(
                       personId: String,
                       personName: String,
                       actionId: String,
                       action: PersonStatAction
                      )
