package org.binary4j

import java.lang.Integer

case class Date(year: Int, month: Int, day: Int)

object Date {
  var xmap: XFunction[((Integer, Integer), Integer), Date] = new XFunction[((Integer, Integer), Integer), Date] {
    def apply(yearMonthDay: ((Integer, Integer), Integer)): Date =
      yearMonthDay match { case ((year, month), day) => Date(year, month, day) }
    def unapply(date: Date): ((Integer, Integer), Integer) = ((date.year, date.month), date.day)
  }
}