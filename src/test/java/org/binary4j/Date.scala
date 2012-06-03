package org.binary4j

import java.lang.Integer

case class Date(year: Int, month: Int, day: Int)

object Date {
  private[binary4j] var xmap: XFunction[Pair[Pair[Integer, Integer], Integer], Date] = new XFunction[Pair[Pair[Integer, Integer], Integer], Date] {
    def apply(yearMonthDay: Pair[Pair[Integer, Integer], Integer]): Date = Date(yearMonthDay._1._1, yearMonthDay._1._2, yearMonthDay._2)
    def unapply(date: Date): Pair[Pair[Integer, Integer], Integer] = Pair.pair(Pair.pair(date.year, date.month), date.day)
  }
}