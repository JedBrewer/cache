import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by jed on 11/10/17.
 *
 * A caching mechanisms that implements a Least Recently Used (LRU) caching scheme using <tt>LinkedHashMap</tt>.
 * Because strict limits have been placed on the cache capacity, it had to be ensured that the
 * underlying <tt>LinkedHashMap</tt> never resizes over this capacity. Thus, keeping in mind that
 * the size of the underlying array is always a power of 2, the load factor is set such that the
 * underlying array will never resize above the largest power of 2 less than capacity.
 * Due to the large number of collisions this generates, it does incur a slowdown, but this should
 * be kept to a minimum with a good hashing function, and is almost certainly faster than a read
 * from the external lookup.
 * TODO: If the String.hashCode proves to lead to bucket clumping, generalize key to allow for user specified hashCode.
 *
 * @param <E>   the record type that this cache queries
 */
public class LRU<E> extends LinkedHashMap<String, E> implements Cacher<E> {
    final int cacheSize;
    final Function<String, E> lookup;

    public LRU(int cacheSize, Function<String, E> lookup) {
        super(cacheSize, cacheSize/roundPow2(cacheSize), true);
        this.cacheSize = cacheSize;
        this.lookup = lookup;
    }

    public E get(String key) {
        E r = super.get(key);
        if (r == null)
            put(key, r = lookup.apply(key));
        return r;
    }

    public int capacity() {
        return cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, E> eldest) {
        return this.size() >= cacheSize;
    }


// Static utilities

    static int roundPow2(int n) {
        int i = 0;
        while (n > 1) {
            n >>>= 1;
            i++;
        }
        return n << i;
    }
}
