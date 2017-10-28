package agency.tango.skald.core.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class TLruCache<K, V> {
  private SkaldLruCache<K, V> cache;
  private TreeMap<Long, K> timestamps = new TreeMap<>();

  public TLruCache(int size) {
    this.cache = new SkaldLruCache<>(size);
  }

  public TLruCache(int size,
      SkaldLruCache.CacheItemRemovedListener<K, V> cacheItemRemovedListener) {
    this(size);
    cache.addCacheItemRemovedListener(cacheItemRemovedListener);
  }

  public void resize(int maxSize) {
    cache.resize(maxSize);
  }

  public V put(K key, V value) {
    timestamps.put(System.currentTimeMillis(), key);
    return cache.put(key, value);
  }

  public V get(K key) {
    V value = cache.get(key);
    if (value != null) {
      removeTimestamp(key);
      timestamps.put(System.currentTimeMillis(), key);
    }
    return value;
  }

  public void trimToSize(int maxSize) {
    cache.trimToSize(maxSize);
  }

  public void evictTo(long value, TimeUnit unit) {
    Long timestamp = System.currentTimeMillis() - unit.toMillis(value);
    Map<Long, K> timestampsToRemove = timestamps.headMap(timestamp);
    for (Iterator<Map.Entry<Long, K>> it = timestampsToRemove.entrySet().iterator();
        it.hasNext(); ) {
      K key = it.next().getValue();
      if (cache.size() > 1) {
        cache.remove(key);
        it.remove();
      }
    }
  }

  public V remove(K key) {
    removeTimestamp(key);
    return cache.remove(key);
  }

  public void evictAll() {
    timestamps.clear();
    cache.evictAll();
  }

  public int size() {
    return cache.size();
  }

  public int maxSize() {
    return cache.maxSize();
  }

  public int hitCount() {
    return cache.hitCount();
  }

  public int missCount() {
    return cache.missCount();
  }

  public int createCount() {
    return cache.createCount();
  }

  public int putCount() {
    return cache.putCount();
  }

  public int evictionCount() {
    return cache.evictionCount();
  }

  public Map<K, V> snapshot() {
    return cache.snapshot();
  }

  public TreeMap<Long, K> getTimestamps() {
    return timestamps;
  }

  private void removeTimestamp(K key) {
    Long timestampToRemove = 0L;
    for (Long timestamp : timestamps.keySet()) {
      if (timestamps.get(timestamp).equals(key)) {
        timestampToRemove = timestamp;
      }
    }
    timestamps.remove(timestampToRemove);
  }
}