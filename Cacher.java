import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by jed on 11/10/17.
 *
 * Cacher interface specifying a general signature for caching mechanisms
 * and providing the infrastructure to specify, register, and spawn these mechanisms as cache instances.
 */
public interface Cacher<E> {
    /** Static registration method for linking caching classes to their associated creator methods.
     * @param clazz     the class type of the caching mechanism being registered
     * @param maker     the creator method of the caching mechanism being registered
     */
    static <E, T extends Cacher<E>> void register(Class<T> clazz,
                                                  BiFunction<Integer, Function<String, E>, T> maker) {
        CacheFactory.register(clazz, maker);
    }

    /**
     * Static method for external users to instantiate a new cache.
     * @param clazz     the class type of the caching mechanism used
     * @param cacheSize the cache capacity
     * @param lookup    the parent query function that is being cached
     * @return          a new cache of the specified type;
     *                  <tt>null</tt> if the cache type has not been registered.
     */
    static <E, T extends Cacher<E>> T New(Class<T> clazz, int cacheSize, Function<String, E> lookup) {
        return CacheFactory.make(clazz, cacheSize, lookup);
    }

    /**
     * query method
     * @param key       the key specifying the record to be returned
     * @return          the record associated with the specified key
     */
    E get(String key);


    /**
     * @return          the number of records currently in the cache
     */
    int size();

    /**
     * @return          the total capacity of the cache
     */
    int capacity();
}


/**
 * A package-private factory the Cacher interface uses to register and create caching mechanisms,
 * both pre-specified and user defined.
 */
class CacheFactory {
    private static Map<Class<? extends Cacher>, BiFunction> registry = new HashMap<>();

    public static <E, T extends Cacher<E>> void register(Class<T> clazz,
                                                         BiFunction<Integer, Function<String, E>, T> maker) {
        registry.put(clazz, maker);
    }

    // This registers the preexisting caching mechanisms, as well as defining the idiomatic form of registration.
    static {
        register(LRU.class, LRU::new);
    }

    public static <E, T extends Cacher<E>> T make(Class<T> clazz, int cacheSize, Function<String, E> lookup) {
        BiFunction<Integer, Function<String, E>, T> maker = registry.get(clazz);
        return maker != null ? (T)registry.get(clazz).apply(cacheSize, lookup) : null;
    }
}