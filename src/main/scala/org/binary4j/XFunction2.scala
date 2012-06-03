package org.binary4j

abstract class XFunction2[A, B, R] extends XFunction[Pair[A, B], R] {
  final def apply(ab: Pair[A, B]): R = apply(ab._1, ab._2)
  def apply(a: A, b: B): R
}