package utils

trait APIException extends RuntimeException{
  def errorMessage: String
}

sealed trait ParsingException extends APIException

object APIException {
  case class MandatoryFieldMissing(fieldName: String) extends ParsingException{
    override def errorMessage: String = s"Field $fieldName is missing in Event"
  }

  case class FieldParsingFailed(fieldName: String) extends ParsingException{
    override def errorMessage: String = s"Field $fieldName parsing failed in Event"
  }

  case class RuntimeError(throwable: Throwable) extends APIException{
    override def errorMessage: String = "Check logs for Error"
  }
}
