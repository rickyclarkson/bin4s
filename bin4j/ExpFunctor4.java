package bin4j;

import static bin4j.Pair.pair;

public abstract class ExpFunctor4<A, B, C, D, R> implements ExpFunctor<Pair<Pair<Pair<A, B>, C>, D>, R>
{
    @Override
    public final R apply(Pair<Pair<Pair<A, B>, C>, D> abcd)
    {
        return apply(abcd._1._1._1, abcd._1._1._2, abcd._1._2, abcd._2);
    }

    public abstract R apply(A a, B b, C c, D d);

    @Override
    public final Pair<Pair<Pair<A, B>, C>, D> unapply(R r)
    {
        Tuple4<A, B, C, D> abcd = unapply4(r);
        return pair(pair(pair(abcd._1, abcd._2), abcd._3), abcd._4);
    }

    public abstract Tuple4<A, B, C, D> unapply4(R r);
}