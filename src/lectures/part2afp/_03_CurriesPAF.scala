package lectures.part2afp

object _03_CurriesPAF extends App {

  // curried functions
  val superAdder: Int => Int => Int = x => y => x + y
  val add3: Int => Int = superAdder(3)
  println(add3(5))
  println(superAdder(3)(5))

  def curriedAdder(x: Int)(y: Int): Int = x + y

  val add4: Int => Int = curriedAdder(4) //  val add4 = curriedAdder(4)(_)
  println(add4(4))

  // lifting = ETA-EXPANSION

  // functions != methods (JVM limitation)
  def inc(x: Int)  = x + 1
  List(1,2,3).map(inc) // ETA-expansion x => int(x)

  // Partial Function applications
  val add5 = curriedAdder(5) _ // ETA-expansion Int => Int

  // EXERCISE
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x:Int)(y: Int) = x + y

  // add7: Int => Int = y => 7 + y
  // as many different implementation of add7 using the above

  val add7 = (x: Int) => simpleAddFunction(7, x)
  println(add7(3))
  val add7_0 = simpleAddFunction(7, _: Int)
  println(add7_0(3))
  val add7_1: Int => Int = simpleAddMethod(7, _)
  println(add7_1(3))
  val add7_2: Int => Int = curriedAdder(7)
  println(add7_2(3))
  val add7_3 = curriedAdder(7)(_) // PAF = partial function application
  println(add7_3(3))
  val add7_4 = curriedAdder(7) _ // PAF
  println(add7_4(3))
  val add7_5 = simpleAddFunction.curried(7)
  println(add7_5(3))

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hello, I'm ", _, ", how are you?") // x: String => concatenator("Hello, I'm ", x, ", how are you?")
  println(insertName("Dario"))

  val fillInTheBlanks = concatenator("Hello, ", _, _) // (x: String, y: String) => concatenator("Hello, ", x, y)
  println(fillInTheBlanks("Dario", ", Scala is awesome"))

  // EXERCISE
  /*
    1. Process a list of numbers and return their string representations with different formats
      use the %4.2.f, %8.6f, %14.12f with a curried formatter function.
  */
  def curriedFormatter(s:String)(number: Double): String = s.format(number)
  val number = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") _
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(number.map(simpleFormat))
  println(number.map(preciseFormat))
  println(number.map(curriedFormatter("%8.6f")))

  /*
    2. difference between
        - functions vs methods
        - parameters: by-name vs 0-lambda
  */

  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  /*
    calling byName and byFunction
    - int
    - method
    - parenMethod
    - lambda
    - PAF
  */

  
}
