package sources.parsers

import utils.Response

object Parsers {

  trait Parser[A] {
    def parse(a: Map[String,String]): Response[A]
  }

  implicit class MapParserOps[A: Parser](a: Map[String,String]) {
    def parse: Response[A] = implicitly[Parser[A]].parse(a)
  }

  implicit class Parsable(json: Map[String,String]) {
    def parseAs[A](implicit parser: Parser[A]): Response[A] = json.parse
  }
}
