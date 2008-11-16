package bin4j;

import java.nio.ByteBuffer;

import static bin4j.Pair.pair;

public final class Format<T>
{
    public final Function<T, ByteBuffer> toBinary;
    public final Function<ByteBuffer, T> fromBinary;

    private Format(Function<T, ByteBuffer> toBinary, Function<ByteBuffer, T> fromBinary)
    {
        this.toBinary = toBinary;
        this.fromBinary = fromBinary;
    }

    public static <T> Format<T> format(Function<T, ByteBuffer> toBinary, Function<ByteBuffer, T> fromBinary)
    {
        return new Format<T>(toBinary, fromBinary);
    }

    public static Format<Integer> integer = format(ByteBuffers.putInt, ByteBuffers.getInt);

    public byte[] toByteArray(T t)
    {
        return toBinary.apply(t).array();
    }

    public T fromByteArray(byte[] array)
    {
        return fromBinary.apply(ByteBuffer.wrap(array));
    }

    public <U> Format<Pair<T, U>> andThen(final Format<U> u)
    {
        Function<ByteBuffer, Pair<T, U>> from = new Function<ByteBuffer, Pair<T, U>>()
        {
            public Pair<T, U> apply(ByteBuffer b)
            {
                return pair(fromBinary.apply(b), u.fromBinary.apply(b));
            }
        };

        Function<Pair<T, U>, ByteBuffer> to = new Function<Pair<T, U>, ByteBuffer>()
        {
            public ByteBuffer apply(Pair<T, U> pair)
            {
                ByteBuffer tbb = toBinary.apply(pair._1);
                ByteBuffer ubb = u.toBinary.apply(pair._2);
                ByteBuffer rbb = ByteBuffer.allocate(tbb.limit() + ubb.limit());
                rbb.put(tbb);
                rbb.put(ubb);
                rbb.position(0);
                return rbb;
            }
        };

        return format(to, from);
    }

    public <U> Format<Pair<T, U>> bind(final Function<T, Format<U>> u)
    {
        Function<ByteBuffer, Pair<T, U>> from = new Function<ByteBuffer, Pair<T, U>>()
        {
            public Pair<T, U> apply(ByteBuffer b)
            {
                T t = fromBinary.apply(b);
                Format<U> fu = u.apply(t);
                return pair(t, fu.fromBinary.apply(b));
            }
        };

        Function<Pair<T, U>, ByteBuffer> to = new Function<Pair<T, U>, ByteBuffer>()
        {
            public ByteBuffer apply(Pair<T, U> pair)
            {
                T t = pair._1;
                Format<U> fu = u.apply(t);
                ByteBuffer tbb = toBinary.apply(t);
                ByteBuffer ubb = fu.toBinary.apply(pair._2);
                ByteBuffer rbb = ByteBuffer.allocate(tbb.limit() + ubb.limit());
                rbb.put(tbb);
                rbb.put(ubb);
                return rbb;
            }
        };

        return format(to, from);
    }

    public static Function<Integer, Format<byte[]>> byteArray = new Function<Integer, Format<byte[]>>()
    {
        public Format<byte[]> apply(final Integer i)
        {
            return format(ByteBuffers.wrap, ByteBuffers.array);
        }
    };
}