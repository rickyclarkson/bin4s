package org.bin4s

case class Date(year: Int, month: Int, day: Int)

object Date {
  var xmap: XFunction[((Int, Int), Int), Date] = new XFunction[((Int, Int), Int), Date] {
    def apply(yearMonthDay: ((Int, Int), Int)): Date =
      yearMonthDay match { case ((year, month), day) => Date(year, month, day) }
    def unapply(date: Date): ((Int, Int), Int) = ((date.year, date.month), date.day)
  }
}