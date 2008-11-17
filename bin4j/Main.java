package bin4j;

import java.nio.ByteBuffer;
import static bin4j.Pair.pair;
import static bin4j.Format.format;

class Main
{
    public static void main(String[] args)
    {
        //serialising and deserialising a single integer.
        equal(Format.integer.fromByteArray(Format.integer.toByteArray(5)), 5);

        //serialising and deserialising a pair of integers.
        Format<Pair<Integer, Integer>> format = Format.integer.andThen(Format.integer);
        equal(format.fromByteArray(format.toByteArray(pair(10, 12))), pair(10, 12));

        //serialising and deserialising a length-encoded sequence of bytes.
        Format<Pair<Integer, byte[]>> lengthEncodedBytes = Format.integer.bind(Format.byteArray);
        ByteBuffer buffer = lengthEncodedBytes.toBinary.apply(pair(3, new byte[]{10, 20, 30}));
        buffer.position(0);
        equal(buffer.getInt(), 3);
        equal(buffer.get(), (byte)10);
        equal(buffer.get(), (byte)20);
        equal(buffer.get(), (byte)30);

        //serialising and deserialising a custom data structure.
        Person person = new Person("Bob", "Hope Lane", new Date(1976, 2, 22));
        Format<Date> dateFormat = Format.integer.andThen(Format.integer).andThen(Format.integer).map(Date.constructor, Date.destructor);
        Format<Person> personFormat = Format.string.andThen(Format.string).andThen(dateFormat).map(Person.constructor, Person.destructor);

        equal(personFormat.fromBinary.apply(personFormat.toBinary.apply(person)), person);

        //two length-encoded sequences of bytes, with both lengths preceding the sequences.
        Format<Pair<byte[], byte[]>> twoByteArrays =
            Format.integer.andThen(Format.integer).bind(new Function<Pair<Integer, Integer>, Format<Pair<byte[], byte[]>>>()
            {
                public Format<Pair<byte[], byte[]>> apply(final Pair<Integer, Integer> lengths)
                {
                    Function<Pair<byte[], byte[]>, ByteBuffer> toBinary = new Function<Pair<byte[], byte[]>, ByteBuffer>()
                    {
                        public ByteBuffer apply(Pair<byte[], byte[]> values)
                        {
                            ByteBuffer b = ByteBuffer.allocate(values._1.length+values._2.length);
                            b.put(values._1);
                            b.put(values._2);
                            b.position(0);
                            return b;
                        }
                    };

                    Function<ByteBuffer, Pair<byte[], byte[]>> fromBinary = new Function<ByteBuffer, Pair<byte[], byte[]>>()
                    {
                        public Pair<byte[], byte[]> apply(ByteBuffer b)
                        {
                            byte[] first = new byte[lengths._1];
                            b.get(first);
                            byte[] second = new byte[lengths._2];
                            b.get(second);
                            return pair(first, second);
                        }
                    };

                    return format(toBinary, fromBinary);
                }
            }).map(new Function<Pair<Pair<Integer, Integer>, Pair<byte[], byte[]>>, Pair<byte[], byte[]>>()
            {
                public Pair<byte[], byte[]> apply(Pair<Pair<Integer, Integer>, Pair<byte[], byte[]>> values)
                {
                    return values._2;
                }
            },new Function<Pair<byte[], byte[]>, Pair<Pair<Integer, Integer>, Pair<byte[], byte[]>>>()
            {
                public Pair<Pair<Integer, Integer>, Pair<byte[], byte[]>> apply(Pair<byte[], byte[]> values)
                {
                    return pair(pair(values._1.length, values._2.length), values);
                }
            });
                                                                
        for (byte b: twoByteArrays.toByteArray(pair(new byte[]{1, 2, 3}, new byte[]{5, 5, 5, 5, 6})))
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

    static Function<Pair<Pair<String, String>, Date>, Person> constructor = new Function<Pair<Pair<String, String>, Date>, Person>()
    {
        public Person apply(Pair<Pair<String, String>, Date> values)
        {
            return new Person(values._1._1, values._1._2, values._2);
        }
    };

    static Function<Person, Pair<Pair<String, String>, Date>> destructor = new Function<Person, Pair<Pair<String, String>, Date>>()
    {
        public Pair<Pair<String, String>, Date> apply(Person person)
        {
            return pair(pair(person.name, person.address), person.dateOfBirth);
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

    static Function<Pair<Pair<Integer, Integer>, Integer>, Date> constructor = new Function<Pair<Pair<Integer, Integer>, Integer>, Date>()
    {
        public Date apply(Pair<Pair<Integer, Integer>, Integer> values)
        {
            return new Date(values._1._1, values._1._2, values._2);
        }
    };

    static Function<Date, Pair<Pair<Integer, Integer>, Integer>> destructor = new Function<Date, Pair<Pair<Integer, Integer>, Integer>>()
    {
        public Pair<Pair<Integer, Integer>, Integer> apply(Date date)
        {
            return pair(pair(date.year, date.month), date.day);
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