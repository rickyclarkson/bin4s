package bin4j;

import java.nio.ByteBuffer;
import static bin4j.Pair.pair;

class Main
{
    public static void main(String[] args)
    {
        Format<Pair<Integer, Integer>> format = Format.integer.andThen(Format.integer);
        equal(format.fromByteArray(format.toByteArray(pair(10, 12))), pair(10, 12));
        equal(Format.integer.fromByteArray(Format.integer.toByteArray(5)), 5);

        Format<Pair<Integer, byte[]>> lengthEncodedBytes = Format.integer.bind(Format.byteArray);
        ByteBuffer buffer = lengthEncodedBytes.toBinary.apply(pair(3, new byte[]{10, 20, 30}));
        buffer.position(0);
        System.out.println(buffer.getInt());
        for (int a=0;a<3;a++)
            System.out.println(buffer.get());
    }

    private static <T> void equal(T t, T u)
    {
        if (!t.equals(u))
            throw new AssertionError(t + "!=" + u);
    }
}