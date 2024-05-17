package model.output

case class Html(underlying: String) extends AnyVal {
  override def toString: String = underlying
}