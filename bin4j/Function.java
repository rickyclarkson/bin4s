package bin4j;

public abstract class Function<T, R>
{
    public abstract R apply(T t);

    public <V> Function<T, V> andThen(final Function<R, V> rv)
    {
        return new Function<T, V>()
        {
            public V apply(T t)
            {
                return rv.apply(Function.this.apply(t));
            }
        };
    }
}