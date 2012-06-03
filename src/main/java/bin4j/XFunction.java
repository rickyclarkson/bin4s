package bin4j;

public interface XFunction<T, R>
{
    R apply(T t);
    T unapply(R r);
}
