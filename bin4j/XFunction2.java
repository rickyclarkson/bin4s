package bin4j;

import static bin4j.Pair.pair;

public abstract class XFunction2<A, B, R> implements XFunction<Pair<A, B>, R>
{
    @Override
    public final R apply(Pair<A, B> ab)
    {
        return apply(ab._1, ab._2);
    }

    public abstract R apply(A a, B b);
}
