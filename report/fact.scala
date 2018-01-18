import stainless.lang._

object f {

  def factorial(n: BigInt): BigInt = {
    require(n >= 0)
    if(n == 0) {
      BigInt(1)
    } else {
      n * factorial(n - 2)
    }
  } ensuring(res => res >= 0)

}
