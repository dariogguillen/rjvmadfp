package lectures.part1as

object _03_AdvancePatternMatching extends App {

  val numbers = List(1)
  val description: Unit = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
    - constants
    - wildcards
    - case classes
    - tuples
    - some special magic like above
  */

  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int): Option[String] = Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 32)

  val greeting = bob match {
    case Person(n, a) => s"Hi my name is $n and I am $a yo"
  }

  println(greeting)

  val bobStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(bobStatus)

  object singleDigit {
    def unapply(x: Int): Boolean = x > -10 && x < 10
  }

  object even {
    def unapply(x: Int): Boolean = x % 2 == 0
  }

  val n: Int = 9
  val mathProperty = n match {
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }

  println(mathProperty)

  // infix patterns
  case class Or[A, B](a: A, b: B) // Either
  val either = Or(2, "two")
  val humanDescription = either match {
    case Or(n, s) => s"$n is written as $s"
  }
  println(humanDescription)

  val humanDescription1 = either match {
    case n Or s => s"$n is written as $s"
  }
  println(humanDescription1)

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decompose = myList match {
    case MyList(1, 2, _*) => "starting with 1,2"
    case _ => "something else"
  }

  // custom return types for unapply
  // isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false

      override def get: String = person.name
    }
  }

  val personWrapper = bob match {
    case PersonWrapper(n) => s"This person is $n"
    case _ => "An alien"
  }
  println(personWrapper)

}
