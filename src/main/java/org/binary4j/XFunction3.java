package org.binary4j;

import static org.binary4j.Pair.pair;

public abstract class XFunction3<A, B, C, R> implements XFunction<Pair<Pair<A, B>, C>, R>
{
    @Override
    public final R apply(Pair<Pair<A, B>, C> abc)
    {
        return apply(abc._1._1, abc._1._2, abc._2);
    }

    public abstract R apply(A a, B b, C c);

    @Override
    public final Pair<Pair<A, B>, C> unapply(R r)
    {
        Tuple3<A, B, C> abc = unapply3(r);
        return pair(pair(abc._1, abc._2), abc._3);
    }

    public abstract Tuple3<A, B, C> unapply3(R r);
}