case class Linear[A](x: A) {
  def ! = x
}

class FileHandler {
  def readLine: (Option[String], Linear[FileHandler]) = ???
  def close: Unit = ???

  def contents: String = {
    val (res, h) = this.readLine
    res match {
      case Some(line) =>
        line + h.!.contents

      case None =>
        h.!.close
        ""
    }
  }
}

class File {
  def open: Linear[FileHandler] = ???
}

