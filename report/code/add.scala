import stainless.lang._
import stainless.annotation._

object test {

  @symeval
  def foo(x: BigInt): BigInt = {
    require(x > 0)

    if (x < 0) {
      foo(x)
    } else {
      x * 2
    }
  }

  def test = {
    foo(-10) == -10
  } holds

}
