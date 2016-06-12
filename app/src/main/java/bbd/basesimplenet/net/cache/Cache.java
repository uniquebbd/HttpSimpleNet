package bbd.basesimplenet.net.cache;

/**
 * 在此写用途
 *
 * @author Dengb
 * @date 2016-6-2 13:41
 */
public interface Cache<K,V> {
    public V get(K key);
    public void put(K key,V value);
    public void remove(K key);
}
