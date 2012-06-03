package org.binary4j

trait XFunction[T, R] {
  def apply(t: T): R
  def unapply(r: R): T
}