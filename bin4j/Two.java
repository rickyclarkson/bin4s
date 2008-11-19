package bin4j;

public class Two<T> extends Pair<T, T>
{
    public Two(T t, T u)
    {
        super(t, u);
    }

    public static <T> Two<T> two(T t, T u)
    {
        return new Two<T>(t, u);
    }
}