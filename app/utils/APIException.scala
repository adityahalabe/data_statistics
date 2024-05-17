package utils

trait APIException extends RuntimeException

sealed trait ParsingException extends APIException

object APIException {
  case class MandatoryFieldMissing(fieldName: String) extends ParsingException

  case class FieldParsingFailed(fieldName: String) extends ParsingException

  case class RuntimeError(throwable: Throwable) extends APIException
}
