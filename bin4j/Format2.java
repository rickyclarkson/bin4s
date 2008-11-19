package bin4j;

import java.nio.ByteBuffer;

public abstract class Format2<T, U> extends Format<Pair<T, U>>
{
    public final ByteBuffer apply(Pair<T, U> pair)
    {
        return apply(pair._1, pair._2);
    }

    public abstract ByteBuffer apply(T t, U u);
}