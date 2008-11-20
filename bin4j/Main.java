package bin4j;

import java.nio.ByteBuffer;
import static bin4j.Pair.pair;
import static bin4j.Tuple3.tuple3;
import static bin4j.ByteBuffers.integer;

class Main
{
    public static void main(String[] args)
    {
        //serialising and deserialising a single integer.
        equal(integer.unapply(integer.apply(5)), 5);

        //serialising and deserialising a pair of integers.
        Format2<Integer, Integer> twoInts = integer.andThen(integer);
        equal(twoInts.unapply(twoInts.apply(10, 12)), pair(10, 12));

        //serialising and deserialising a length-encoded sequence of bytes.  The call to map just makes the Integer disappear from the resulting Format type.
        Format<byte[]> lengthEncodedBytes = ByteBuffers.integer.bind(ByteBuffers.byteArray).map(new XFunction2<Integer, byte[], byte[]>()
        {
            public byte[] apply(Integer length, byte[] array)
            {
                return array;
            }
            
            public Pair<Integer, byte[]> unapply(byte[] array)
            {
                return pair(array.length, array);
            }
        });

        equal(lengthEncodedBytes.unapply(lengthEncodedBytes.apply(new byte[]{1,3,5,7,9}))[3], (byte)7);

        //serialising and deserialising a custom data structure.
        Person person = new Person("Bob", "Hope Lane", new Date(1976, 2, 22));
        Format<Date> dateFormat = ByteBuffers.integer.andThen(ByteBuffers.integer).andThen(ByteBuffers.integer).map(Date.xmap);
        Format<Person> personFormat = Format.string.andThen(Format.string).andThen(dateFormat).map(Person.xmap);

        equal(personFormat.unapply(personFormat.apply(person)), person);

        //two length-encoded sequences of bytes, with both lengths preceding the sequences.
        Format<Pair<byte[], byte[]>> twoByteArrays =
            ByteBuffers.integer.andThen(ByteBuffers.integer).bind(new Function2<Integer, Integer, Format<Pair<byte[], byte[]>>>()
            {
                public Format2<byte[], byte[]> apply(final Integer firstLength, final Integer secondLength)
                {
                    return new Format2<byte[], byte[]>()
                    {
                        public ByteBuffer apply(byte[] first, byte[] second)
                        {
                            if (first.length != firstLength || second.length != secondLength)
                                throw null;

                            ByteBuffer b = ByteBuffer.allocate(firstLength + secondLength);
                            b.put(first);
                            b.put(second);
                            b.position(0);
                            return b;
                        }
                    
                        public Pair<byte[], byte[]> unapply(ByteBuffer buffer)
                        {
                            byte[] first = new byte[firstLength];
                            byte[] second = new byte[secondLength];
                            buffer.get(first);
                            buffer.get(second);
                            return pair(first, second);
                        }
                    };
                }
            }).map(new XFunction3<Integer, Integer, Pair<byte[], byte[]>, Pair<byte[], byte[]>>()
            {
                public Pair<byte[], byte[]> apply(Integer firstLength, Integer secondLength, Pair<byte[], byte[]> arrays)
                {
                    return arrays;
                }

                public Tuple3<Integer, Integer, Pair<byte[], byte[]>> unapply3(Pair<byte[], byte[]> arrays)
                {
                    return tuple3(arrays._1.length, arrays._2.length, arrays);
                }
            });
                                                                
        for (byte b: twoByteArrays.apply(pair(new byte[]{1, 2, 3}, new byte[]{5, 5, 5, 5, 6})).array())
            System.out.println(b);
    }

    private static <T> void equal(T t, T u)
    {
        if (!t.equals(u))
            throw new AssertionError(t + "!=" + u + "( t.getClass = "+t.getClass()+", u.getClass = "+u.getClass()+")");
    }
}

class Person
{
    String name;
    String address;
    Date dateOfBirth;

    Person(String name, String address, Date dateOfBirth)
    {
        this.name=name;
        this.address=address;
        this.dateOfBirth=dateOfBirth;
    }

    static XFunction3<String, String, Date, Person> xmap = new XFunction3<String, String, Date, Person>()
    {
        public Person apply(String name, String address, Date dateOfBirth)
        {
            return new Person(name, address, dateOfBirth);
        }

        public Tuple3<String, String, Date> unapply3(Person person)
        {
            return tuple3(person.name, person.address, person.dateOfBirth);
        }
    };

    public String toString()
    {
        return "Person(name="+name+", address="+address+", dateOfBirth="+dateOfBirth+")";
    }

    public boolean equals(Object other)
    {
        if (other instanceof Person)
        {
            Person p = (Person)other;
            return p.name.equals(name) && p.address.equals(address) && p.dateOfBirth.equals(dateOfBirth);
        }

        return false;
    }
}

class Date
{
    int year;
    int month;
    int day;

    public Date(int year, int month, int day)
    {
        this.year = year;
        this.month = month;
        this.day= day;
    }

    static XFunction3<Integer, Integer, Integer, Date> xmap = new XFunction3<Integer, Integer, Integer, Date>()
    {
        public Date apply(Integer year, Integer month, Integer day)
        {
            return new Date(year, month, day);
        }

        public Tuple3<Integer, Integer, Integer> unapply3(Date date)
        {
            return tuple3(date.year, date.month, date.day);
        }
    };

    public String toString()
    {
        return "Date[year="+year+", month="+month+", day="+day+"]";
    }

    public boolean equals(Object other)
    {
        if (other instanceof Date)
        {
            Date o = (Date)other;
            return o.day == day && o.month == month && o.year == year;
        }

        return false;
    }
}
