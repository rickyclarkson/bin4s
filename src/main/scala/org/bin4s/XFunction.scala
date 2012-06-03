package org.bin4s

trait XFunction[T, R] {
  def apply(t: T): R
  def unapply(r: R): T
}