package util

import akka.actor.ActorSystem
import org.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import utils.{APIException, ResponseT}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

trait BaseSpec extends PlaySpec with MockitoSugar{

  implicit val sys = ActorSystem("Test")
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  implicit class ResponseTOps[T](response: ResponseT[T]) {
    def await: Either[APIException, T] = Await.result(response.value, 30.seconds)
  }
}
