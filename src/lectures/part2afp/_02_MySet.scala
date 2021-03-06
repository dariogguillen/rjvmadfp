package lectures.part2afp

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {
  // EXERCISE - implement a functional set

  def apply(elem: A): Boolean = contains(elem)
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(f: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  // EXERCISE 2:
  // - remove an element
  // - intersection with another set
  // - difference with another set

  def remove(elem: A): MySet[A]
  def intersection(anotherSet: MySet[A]): MySet[A]
  def difference(anotherSet: MySet[A]): MySet[A]

}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false
  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  override def filter(f: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()

  override def remove(elem: A): MySet[A] = this
  override def intersection(anotherSet: MySet[A]): MySet[A] = this
  override def difference(anotherSet: MySet[A]): MySet[A] = this

}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(head)
  override def +(elem: A): MySet[A] =
    if (this apply elem) this
    else new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)
  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)
  override def filter(f: A => Boolean): MySet[A] = {
    val filtered = tail.filter(f)
    if (f(head)) filtered + head
    else filtered
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def remove(elem: A): MySet[A] = filter(_ != elem)
  override def intersection(anotherSet: MySet[A]): MySet[A] = flatMap (el => anotherSet filter (_ == el))
  override def difference(anotherSet: MySet[A]): MySet[A] = filter(a => anotherSet(a)) // ???

}

object MySet {
  def apply[A](el: A*): MySet[A] = {
    @tailrec
    def build(seq: Seq[A], acc: MySet[A]): MySet[A] =
      if (seq.isEmpty) acc
      else build(seq.tail, acc + seq.head)

    build(el.toList, new EmptySet[A])
  }
}

object Test extends App {
  val s = MySet(1, 2, 3, 4)

  //  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, x * 10)) filter (_ % 2 == 0) foreach println
  //
//    (MySet(1, 2, 3 ,8,0) remove 8) foreach println

//  MySet(1, 2, 3, 4, 5, 8) intersection MySet(3, 4, 5, 6) foreach println
  MySet(1, 2, 3, 4) difference MySet(3, 4, 5, 6) foreach println
}


