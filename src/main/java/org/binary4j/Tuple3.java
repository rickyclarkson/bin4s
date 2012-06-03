package org.binary4j;

public final class Tuple3<A, B, C>
{
    public final A _1;
    public final B _2;
    public final C _3;

    public Tuple3(A _1, B _2, C _3)
    {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    public static <A, B, C> Tuple3<A, B, C> tuple3(A a, B b, C c)
    {
        return new Tuple3<A, B, C>(a, b, c);
    }
}
