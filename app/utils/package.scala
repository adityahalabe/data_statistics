import cats.data.EitherT
import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._

package object utils {

  private type ResponseTPure[F[_], E <: Exception, A] = EitherT[F, E, A]

  type ResponseT[A] = ResponseTPure[Future, APIException, A]

  type Response[A] = Either[APIException, A]

  object ResponseT {
    def apply[A](failure: APIException)(implicit ec: ExecutionContext): ResponseT[A] = EitherT.leftT[Future, A](failure)
  }
}
