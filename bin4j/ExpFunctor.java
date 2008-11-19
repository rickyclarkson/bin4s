package bin4j;

public interface ExpFunctor<T, R>
{
    R apply(T t);
    T unapply(R r);

    
}