package org.binary4j

case class Person(name: String, address: String, dateOfBirth: Date)

object Person {
  var xmap: XFunction[Pair[Pair[String, String], Date], Person] = new XFunction[Pair[Pair[String, String], Date], Person] {
    def apply(nameAddressDateOfBirth: Pair[Pair[String, String], Date]): Person = new Person(nameAddressDateOfBirth._1._1, nameAddressDateOfBirth._1._2, nameAddressDateOfBirth._2)

    def unapply(person: Person): Pair[Pair[String, String], Date] = Pair.pair(Pair.pair(person.name, person.address), person.dateOfBirth)
  }
}