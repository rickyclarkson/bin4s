package bin4j;

import java.nio.ByteBuffer;

public class ByteBuffers
{
    public static Function<Integer, ByteBuffer> putInt = new Function<Integer, ByteBuffer>()
    {
        public ByteBuffer apply(Integer i)
        {
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(i);
            b.position(0);
            return b;
        }
    };

    public static Function<ByteBuffer, Integer> getInt = new Function<ByteBuffer, Integer>()
    {
        public Integer apply(ByteBuffer b)
        {
            return b.getInt();
        }
    };

    public static Function<byte[], ByteBuffer> wrap = new Function<byte[], ByteBuffer>()
    {
        public ByteBuffer apply(byte[] b)
        {
            return ByteBuffer.wrap(b);
        }
    };

    public static Function<ByteBuffer, byte[]> array = new Function<ByteBuffer, byte[]>()
    {
        public byte[] apply(ByteBuffer b)
        {
            return b.array();
        }
    };
}