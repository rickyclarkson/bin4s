package bin4j;

import java.nio.ByteBuffer;

import static bin4j.Pair.pair;
import java.io.UnsupportedEncodingException;

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

    public static Format<String> string;
    static
    {
        Function<String, ByteBuffer> toBinary = new Function<String, ByteBuffer>()
        {
            public ByteBuffer apply(String s)
            {
                byte[] bytes;
                try
                {
                    bytes = s.getBytes("US-ASCII");
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new RuntimeException(e);
                }
                ByteBuffer result = ByteBuffer.allocate(4 + bytes.length);
                result.putInt(bytes.length);
                result.put(bytes);
                result.position(0);
                return result;
            }
        };

        Function<ByteBuffer, String> fromBinary = new Function<ByteBuffer, String>()
        {
            public String apply(ByteBuffer b)
            {
                int length = b.getInt();
                byte[] bytes = new byte[length];
                b.get(bytes);
                try
                {
                    return new String(bytes, "US-ASCII");
                }
                catch (UnsupportedEncodingException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };

        string = new Format<String>(toBinary, fromBinary);
    }

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

    public <U> Format<U> map(Function<T, U> toU, Function<U, T> toT)
    {
        return new Format<U>(toT.andThen(toBinary), fromBinary.andThen(toU));
    }

    public <U, V> Format<V> bindAndMap(Function<T, Format<U>> u, Function<Pair<T, U>, V> toV, Function<V, Pair<T, U>> fromV)
    {
        return bind(u).map(toV, fromV);
    }
}