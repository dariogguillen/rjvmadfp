package lectures.part2afp

import scala.annotation.tailrec

object _04_LazyEvaluation extends App {

  lazy val x: Int = {
    println("hello")
    53
  }
  println(x)
  println(x)

  // examples of implications:
  // 1 side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }
  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")

  // in conjunction with call by name
  def byNameMethod(n: => Int): Int = n + n + n + 1
  def retrieveMagicValue = {
    println("waiting")
    Thread.sleep(1000)
    42
  }
  println(byNameMethod(retrieveMagicValue))

  // use lazy vals  CALL BY NEED
  def byNameMethod1(n: => Int): Int = {
    lazy val t =  n // only evaluated once
    t + t + t + 1
  }
  def retrieveMagicValue1 = {
    println("waiting")
    Thread.sleep(1000)
    42
  }
  println(byNameMethod1(retrieveMagicValue1))

  // filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)

  val lt30 = numbers.filter(lessThan30)
  println(lt30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30lazy = numbers.withFilter(lessThan30) // lazy vals under the hood
  val gt20lazy = lt30lazy.withFilter(greaterThan20)
  println(gt20lazy)
  gt20lazy.foreach(println)

  // for-comprehensions use withFilter with guards
  for {
    a <- List(1,2,3) if a % 2 == 0 // use  lazy vals
  } yield a + 1
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1)

}

/*
 Exercise: implement a lazily evaluated, singly linked STREAM of elements

 naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
 naturals.take(100) // lazily evaluated stream of first 100 naturals (finite stream)
 naturals.foreach(println) // will crash - infinite!
*/
abstract class MyStream[+A] {
  def isEmpty: Boolean
  def head: A
  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B]
  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B]

  def foreach(f: A => Unit): Unit
  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]): MyStream[B]
  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A] // takes the first n elements out of this stream
  def takeAsList(n: Int): List[A] = take(n).toList()

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] =
    if (isEmpty) acc
    else tail.toList(head :: acc)
}

object EmtpyMyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true
  override def head: Nothing = throw new NoSuchElementException
  override def tail: MyStream[Nothing] = throw  new NoSuchElementException

  override def #::[B >: Nothing](element: B): MyStream[B] = new NonEmtpyMyStream[B](element, this)
  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()
  override def map[B](f: Nothing => B): MyStream[B] = this
  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this
}

class NonEmtpyMyStream[+A](h:A, t: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false
  override def head: A = h
  override def tail: MyStream[A] = t

  override def #::[B >: A](element: B): MyStream[B] = new NonEmtpyMyStream[B](element, this)
  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new NonEmtpyMyStream[B](h, t ++ anotherStream)

  override def foreach(f: A => Unit): Unit = {
    f(h)
    t.foreach(f)
  }
  override def map[B](f: A => B): MyStream[B] = new NonEmtpyMyStream[B](f(h), t.map(f))
  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(h) ++ t.flatMap(f)
  override def filter(predicate: A => Boolean): MyStream[A] =
    if(predicate(h)) new NonEmtpyMyStream[A](h,t.filter(predicate))
    else t.filter(predicate)

  override def take(n: Int): MyStream[A] = {
    if(n == 0) EmtpyMyStream
    else if (n == 1) new NonEmtpyMyStream[A](h, EmtpyMyStream)
    else new NonEmtpyMyStream[A](h, t.take(n - 1))
  }
  override def takeAsList(n: Int): List[A] = take(n).toList()
}




object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = new NonEmtpyMyStream[A](start, from(generator(start))(generator) )
}

object MyStreamPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)
  println(naturals.tail.tail.tail)

  val startFrom0 = 0 #:: naturals
  println(startFrom0.head)
  startFrom0.take(10000).foreach(println)

  println(startFrom0.map(_ * 2).takeAsList(100))
  println(startFrom0.flatMap(a => new NonEmtpyMyStream(a, new NonEmtpyMyStream(a + 3, EmtpyMyStream))).take(100).toList())

  startFrom0.filter(_ % 2 == 0).take(20).foreach(println)
}
