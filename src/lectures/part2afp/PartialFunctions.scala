package lectures.part2afp

object PartialFunctions extends App {
  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match { // {1,2,5} => Int
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  println(aPartialFunction(2))
  //  println(aPartialFunction(12))

  // PF utilities
  println(aPartialFunction.isDefinedAt(76))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(12))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  // PF extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  print(aMappedList)

  /*
    Note: PF can only have 1 parameter type
  */

  /**
   * Exercises
   * 1 - construct a PF instance yourself (anonymous class)
   * 2 - dumb chatbot as a PF
   */

  scala.io.Source.stdin.getLines().map {
    case "hola" => "mundo"
    case "hola que hace" => "o que hace"
    case "mundo" => "hola"
  }.foreach(println)

  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 || x == 5

    override def apply(x: Int): Int = x match { // {1,2,5} => Int
      case 1 => 42
      case 2 => 56
      case 5 => 999
    }
  }

}
