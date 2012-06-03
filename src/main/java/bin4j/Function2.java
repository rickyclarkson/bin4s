package bin4j;

abstract class Function2<T, U, R> extends Function<Pair<T, U>, R>
{
    @Override
    public final R apply(Pair<T, U> pair)
    {
        return apply(pair._1, pair._2);
    }

    public abstract R apply(T t, U u);
}