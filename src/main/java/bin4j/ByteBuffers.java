package bin4j;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBuffers
{
    public static Format<Integer> integer = new Format<Integer>()
    {
        public ByteBuffer apply(Integer i)
        {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(i);
            b.position(0);
            return b;
        }

        public Integer unapply(ByteBuffer b)
        {
            return b.getInt();
        }
    };

    public static Format<Short> littleEndianShort = new Format<Short>()
    {
        public ByteBuffer apply(Short s)
        {
            ByteBuffer b = ByteBuffer.allocate(2);
            b.order(ByteOrder.LITTLE_ENDIAN);
            b.putShort(s);
            b.order(ByteOrder.BIG_ENDIAN);
            b.position(0);
            return b;
        }

        public Short unapply(ByteBuffer b)
        {
            ByteOrder order = b.order();
            b.order(ByteOrder.LITTLE_ENDIAN);
            short s = b.getShort();
            b.order(order);
            return s;
        }
    };

    public static Function<byte[], ByteBuffer> wrap = new Function<byte[], ByteBuffer>()
    {
        public ByteBuffer apply(byte[] b)
        {
            return ByteBuffer.wrap(b);
        }
    };

    public static Function<Integer, Format<byte[]>> byteArray = new Function<Integer, Format<byte[]>>()
    {
        public Format<byte[]> apply(final Integer length)
        {
            return new Format<byte[]>()
            {
                public ByteBuffer apply(byte[] array)
                {
                    if (array.length != length)
                        throw null;

                    return ByteBuffer.wrap(array);
                }

                public byte[] unapply(ByteBuffer buffer)
                {
                    byte[] result = new byte[length];
                    buffer.get(result);
                    return result;
                }
            };
        }
    };

    public static Function<ByteBuffer, byte[]> array = new Function<ByteBuffer, byte[]>()
    {
        public byte[] apply(ByteBuffer b)
        {
            return b.array();
        }
    };

    public static ByteBuffer sequence(ByteBuffer a, ByteBuffer b)
    {
        ByteBuffer result = ByteBuffer.allocate(a.limit() + b.limit());
        result.put(a);
        result.put(b);
        result.position(0);
        return result;
    }
}