package org.binary4j;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class Binary4JTest
{
    @Test
    public void singleInteger() {
        final int actual = ByteBuffers.integer.unapply(ByteBuffers.integer.apply(5));
        assertEquals(5, actual);
    }

    @Test
    public void pairOfIntegers() {
        Format2<Integer, Integer> twoInts = ByteBuffers.integer.andThen(ByteBuffers.integer);
        assertEquals(Pair.pair(10, 12), twoInts.unapply(twoInts.apply(10, 12)));
    }

    @Test
    public void lengthEncodedBytes() {
        //serialising and deserialising a length-encoded sequence of bytes.  The call to map just makes the Integer disappear from the resulting Format type.
        Format<byte[]> lengthEncodedBytes = ByteBuffers.integer.bind(ByteBuffers.byteArray).map(new XFunction2<Integer, byte[], byte[]>()
        {
            public byte[] apply(Integer length, byte[] array)
            {
                return array;
            }
            
            public Pair<Integer, byte[]> unapply(byte[] array)
            {
                return Pair.pair(array.length, array);
            }
        });

        assertEquals(7, lengthEncodedBytes.unapply(lengthEncodedBytes.apply(new byte[]{1,3,5,7,9}))[3]);
    }

    @Test
    public void customDataStructure() {
        //serialising and deserialising a custom data structure.
        Person person = new Person("Bob", "Hope Lane", new Date(1976, 2, 22));
        Format<Date> dateFormat = ByteBuffers.integer.andThen(ByteBuffers.integer).andThen(ByteBuffers.integer).map(Date.xmap);
        Format<Person> personFormat = Format.string.andThen(Format.string).andThen(dateFormat).map(Person.xmap);

        assertEquals(person, personFormat.unapply(personFormat.apply(person)));
    }
}