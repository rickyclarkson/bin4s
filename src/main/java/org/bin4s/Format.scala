package org.bin4s

import java.nio.ByteBuffer
import java.io.UnsupportedEncodingException

abstract class Format[T] extends XFunction[T, ByteBuffer] {
  def andThen[U](other: Format[U]): Format[(T, U)] = new Format[(T, U)] {
    def apply(tu: (T, U)): ByteBuffer = tu match { case (t, u) => ByteBuffers.sequence(Format.this.apply(t), other.apply(u)) }
    def unapply(b: ByteBuffer): (T, U) = (Format.this.unapply(b), other.unapply(b))
  }

  def bind[U](uf: T => Format[U]): Format[(T, U)] = new Format[(T, U)] {
    def apply(tu: (T, U)): ByteBuffer = {
      val fu: Format[U] = uf.apply(tu._1)
      ByteBuffers.sequence(Format.this.apply(tu._1), fu.apply(tu._2))
    }

    def unapply(b: ByteBuffer): (T, U) = {
      val t: T = Format.this.unapply(b)
      val fu: Format[U] = uf.apply(t)
      (t, fu.unapply(b))
    }
  }

  def map[U](xmap: XFunction[T, U]): Format[U] = new Format[U] {
    def apply(u: U): ByteBuffer = Format.this.apply(xmap.unapply(u))
    def unapply(b: ByteBuffer): U = xmap.apply(Format.this.unapply(b))
  }
}

object Format {
  var string: Format[String] = new Format[String] {
    def apply(s: String): ByteBuffer = {
      var bytes: Array[Byte] = null
      try {
        bytes = s.getBytes("US-ASCII")
      }
      catch {
        case e: UnsupportedEncodingException => {
          throw new RuntimeException(e)
        }
      }
      val result: ByteBuffer = ByteBuffer.allocate(4 + bytes.length)
      result.putInt(bytes.length)
      result.put(bytes)
      result.position(0)
      result
    }

    def unapply(b: ByteBuffer): String = {
      val length: Int = b.getInt
      val bytes: Array[Byte] = new Array[Byte](length)
      b.get(bytes)
      try
        new String(bytes, "US-ASCII")
      catch {
        case e: UnsupportedEncodingException => {
          throw new RuntimeException(e)
        }
      }
    }
  }
}
