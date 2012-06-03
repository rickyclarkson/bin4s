package org.binary4j

import java.nio.ByteBuffer
import java.nio.ByteOrder

import java.lang.Integer

object ByteBuffers {
  def sequence(a: ByteBuffer, b: ByteBuffer): ByteBuffer = {
    val result: ByteBuffer = ByteBuffer.allocate(a.limit + b.limit)
    result.put(a)
    result.put(b)
    result.position(0)
    result
  }

  var integer: Format[Integer] = new Format[Integer] {
    def apply(i: Integer): ByteBuffer = {
      val b: ByteBuffer = ByteBuffer.allocate(4)
      b.putInt(i)
      b.position(0)
      b
    }

    def unapply(b: ByteBuffer): Integer = b.getInt
  }
  var littleEndianShort: Format[Short] = new Format[Short] {
    def apply(s: Short): ByteBuffer = {
      val b: ByteBuffer = ByteBuffer.allocate(2)
      b.order(ByteOrder.LITTLE_ENDIAN)
      b.putShort(s)
      b.order(ByteOrder.BIG_ENDIAN)
      b.position(0)
      b
    }

    def unapply(b: ByteBuffer): Short = {
      val order: ByteOrder = b.order
      b.order(ByteOrder.LITTLE_ENDIAN)
      val s: Short = b.getShort
      b.order(order)
      s
    }
  }
  var wrap: Function[Array[Byte], ByteBuffer] = new Function[Array[Byte], ByteBuffer] {
    def apply(b: Array[Byte]): ByteBuffer = ByteBuffer.wrap(b)
  }
  var byteArray: Function[Integer, Format[Array[Byte]]] = new Function[Integer, Format[Array[Byte]]] {
    def apply(length: Integer): Format[Array[Byte]] = {
      new Format[Array[Byte]] {
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
    }
  }
  var array: Function[ByteBuffer, Array[Byte]] = new Function[ByteBuffer, Array[Byte]] {
    def apply(b: ByteBuffer): Array[Byte] = b.array
  }
}