package org.bin4s

case class Person(name: String, address: String, dateOfBirth: Date)

object Person {
  val xmap: XFunction[((String, String), Date), Person] = new XFunction[((String, String), Date), Person] {
    def apply(nameAddressDateOfBirth: ((String, String), Date)): Person =
      nameAddressDateOfBirth match { case ((name, address), dateOfBirth) => Person(name, address, dateOfBirth) }

    def unapply(person: Person): ((String, String), Date) = ((person.name, person.address), person.dateOfBirth)
  }
}