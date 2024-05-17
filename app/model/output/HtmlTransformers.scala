package model.output

object HtmlTransformers {

  trait HtmlTransformer[A] {
    def toHtml(a: A): Html
  }

  implicit class HtmlTransformerOps[A: HtmlTransformer](a: A) {
    def toHtml: Html =
      implicitly[HtmlTransformer[A]].toHtml(a)
  }
}



