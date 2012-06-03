package bin4j;

import java.nio.ByteBuffer;

import static bin4j.Pair.pair;
import java.io.UnsupportedEncodingException;

public abstract class Format<T> implements XFunction<T, ByteBuffer>
{
    public static Format<String> string = new Format<String>()
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

        public String unapply(ByteBuffer b)
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

    public <U> Format2<T, U> andThen(final Format<U> other)
    {
        return new Format2<T, U>()
        {
            public ByteBuffer apply(T t, U u)
            {
                return ByteBuffers.sequence(Format.this.apply(t), other.apply(u));
            }

            public Pair<T, U> unapply(ByteBuffer b)
            {
                return pair(Format.this.unapply(b), other.unapply(b));
            }
        };
    }

    public <U> Format2<T, U> bind(final Function<T, Format<U>> uf)
    {
        return new Format2<T, U>()
        {
            public ByteBuffer apply(T t, U u)
            {
                Format<U> fu = uf.apply(t);
                return ByteBuffers.sequence(Format.this.apply(t), fu.apply(u));
            }

            public Pair<T, U> unapply(ByteBuffer b)
            {
                T t = Format.this.unapply(b);
                Format<U> fu = uf.apply(t);
                return pair(t, fu.unapply(b));
            }                
        };
    }

    public <U> Format<U> map(final XFunction<T, U> xmap)
    {
        return new Format<U>()
        {
            public ByteBuffer apply(U u)
            {
                return Format.this.apply(xmap.unapply(u));
            }

            public U unapply(ByteBuffer b)
            {
                return xmap.apply(Format.this.unapply(b));
            }
        };
    }
}
