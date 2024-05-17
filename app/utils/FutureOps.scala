package utils

import cats.implicits._
import utils.APIException.RuntimeError

import scala.concurrent.{ExecutionContext, Future}

object FutureOps {

  implicit class FutureOps[T](future: Future[T]) {

    def toEitherT(errorFn: PartialFunction[Throwable, APIException] = PartialFunction.empty)(implicit ec: ExecutionContext): ResponseT[T] = {
      future.attemptT.leftMap{
        case error if errorFn.isDefinedAt(error) => errorFn(error)
        case runtimeError => RuntimeError(runtimeError)
      }
    }
  }
}
