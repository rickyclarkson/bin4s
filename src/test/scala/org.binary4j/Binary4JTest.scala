package org.binary4j

import org.junit.Test
import org.junit.Assert.assertEquals

import java.lang.Integer

final class Binary4JTest {
  @Test def singleInteger() {
    val actual: Int = ByteBuffers.integer.unapply(ByteBuffers.integer.apply(5))
    assertEquals(5, actual)
  }

  @Test def pairOfIntegers() {
    val twoInts: Format[Pair[Integer, Integer]] = ByteBuffers.integer.andThen(ByteBuffers.integer)
    assertEquals(Pair.pair(10, 12), twoInts.unapply(twoInts.apply(Pair.pair(10, 12))))
  }

  @Test def lengthEncodedBytes() {
    val lengthEncodedBytes: Format[Array[Byte]] = ByteBuffers.integer.bind(ByteBuffers.byteArray).map(new XFunction2[Integer, Array[Byte], Array[Byte]] {
      def apply(length: Integer, array: Array[Byte]): Array[Byte] = array

      def unapply(array: Array[Byte]): Pair[Integer, Array[Byte]] = Pair.pair(array.length, array)
    })
    assertEquals(7, lengthEncodedBytes.unapply(lengthEncodedBytes.apply(Array[Byte](1, 3, 5, 7, 9)))(3))
  }

  @Test
  def customDataStructure() {
    //serialising and deserialising a custom data structure.
    val person = new Person("Bob", "Hope Lane", new Date(1976, 2, 22))

    val dateFormat: Format[Date] = ByteBuffers.integer.andThen(ByteBuffers.integer).andThen(ByteBuffers.integer).map(Date.xmap)

    val personFormat: Format[Person] = Format.string.andThen(Format.string).andThen(dateFormat).map(Person.xmap)
    assertEquals(person, personFormat.unapply(personFormat.apply(person)))
  }
}