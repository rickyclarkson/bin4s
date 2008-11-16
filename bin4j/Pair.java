package bin4j;

public class Pair<T, U>
{
    public final T _1;
    public final U _2;

    public Pair(T _1, U _2)
    {
        this._1 = _1;
        this._2 = _2;
    }

    public static <T, U> Pair<T, U> pair(T t, U u)
    {
        return new Pair<T, U>(t, u);
    }

    public boolean equals(Object other)
    {
        return ((Pair)other)._1.equals(_1) && ((Pair)other)._2.equals(_2);
    }
}