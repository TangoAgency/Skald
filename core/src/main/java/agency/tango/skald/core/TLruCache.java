package agency.tango.skald.core;

import android.util.LruCache;

import java.util.Map;
import java.util.TreeMap;

public class TLruCache<K, V> {
  private LruCache<K, V> cache;
  private TreeMap<Long, K> timestamps = new TreeMap<>();
  private CacheItemRemovedListener<K, V> cacheItemRemovedListener;

  public TLruCache(int size) {
    this.cache = new LruCache<>(size);
  }

  public void resize(int maxSize) {
    cache.resize(maxSize);
  }

  public V put(K key, V value) {
    timestamps.put(System.currentTimeMillis(), key);
    return cache.put(key, value);
  }

  public V get(K key) {
    return cache.get(key);
  }

  public void trimToSize(int maxSize) {
    cache.trimToSize(maxSize);
  }

  public void evictTo(int minutes, int seconds) {
    Long timestamp = System.currentTimeMillis() - (minutes * 60 + seconds) * 1000;
    for (Long key : timestamps.headMap(timestamp).keySet()) {
      cache.remove(timestamps.get(key));
      timestamps.remove(key);
    }
  }

  public V remove(K key) {
    for (Long timestamp : timestamps.keySet()) {
      if (timestamps.get(timestamp).equals(key)) {
        timestamps.remove(timestamp);
      }
    }
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

  public void setCacheItemRemovedListener(CacheItemRemovedListener<K, V> cacheItemRemovedListener) {
    this.cacheItemRemovedListener = cacheItemRemovedListener;
  }

  protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
    if (cacheItemRemovedListener != null) {
      cacheItemRemovedListener.release(key, oldValue);
    }
  }

  protected V create(K key) {
    return null;
  }

  protected int sizeOf(K key, V value) {
    return 1;
  }

  public interface CacheItemRemovedListener<K, V> {
    void release(K key, V value);
  }
}