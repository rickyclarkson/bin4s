package org.binary4j

import java.nio.ByteBuffer
import java.nio.ByteOrder

object ByteBuffers {
  def sequence(a: ByteBuffer, b: ByteBuffer): ByteBuffer = {
    val result: ByteBuffer = ByteBuffer.allocate(a.limit + b.limit)
    result.put(a)
    result.put(b)
    result.position(0)
    result
  }

  val integer: Format[Int] = new Format[Int] {
    override def apply(i: Int) = {
      val b: ByteBuffer = ByteBuffer.allocate(4)
      b.putInt(i)
      b.position(0)
      b
    }

    override def unapply(b: ByteBuffer) = b.getInt
  }

  val littleEndianShort: Format[Short] = new Format[Short] {
    override def apply(s: Short) = {
      val b: ByteBuffer = ByteBuffer.allocate(2)
      b.order(ByteOrder.LITTLE_ENDIAN)
      b.putShort(s)
      b.order(ByteOrder.BIG_ENDIAN)
      b.position(0)
      b
    }

    override def unapply(b: ByteBuffer) = {
      val order: ByteOrder = b.order
      b.order(ByteOrder.LITTLE_ENDIAN)
      val s: Short = b.getShort
      b.order(order)
      s
    }
  }

  val wrap: Array[Byte] => ByteBuffer = b => ByteBuffer.wrap(b)

  val byteArray: Int => Format[Array[Byte]] = length => new Format[Array[Byte]] {
    def apply(array: Array[Byte]): ByteBuffer = {
      if (array.length != length) throw null
      ByteBuffer.wrap(array)
    }

    def unapply(buffer: ByteBuffer): Array[Byte] = {
      val result: Array[Byte] = new Array[Byte](length)
      buffer.get(result)
      result
    }
  }

  val array: ByteBuffer => Array[Byte] = _.array
}