package agency.tango.skald.core;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TLruCacheInstrumentedTest {
  @Test
  public void resize() {
    TLruCache cache = new TLruCache(3);
    cache.resize(5);
    assertEquals(5, cache.maxSize());

    cache.resize(2);
    assertEquals(2, cache.maxSize());
  }

  @Test
  public void put_valueIsNotInCache() {
    TLruCache<Object, Object> cache = new TLruCache<>(3);
    Object key = new Object();
    Object value = new Object();

    cache.put(key, value);
    Map snapshot = cache.snapshot();

    assertTrue(cache.getTimestamps().containsValue(key));
    assertTrue(snapshot.containsKey(key));
    assertTrue(snapshot.containsValue(value));
    assertEquals(1, cache.putCount());
  }

  @Test
  public void put_valueIsAlreadyInCache_shouldReturnObject() {
    TLruCache<Object, Object> cache = new TLruCache<>(2);
    Object key = new Object();
    Object value = new Object();

    cache.put(key, value);
    Object returnedObject = cache.put(key, value);

    assertEquals(value, returnedObject);
    assertEquals(1, cache.size());
    assertEquals(2, cache.putCount());
  }

  @Test
  public void get_valueInCache_shouldReturnObject() {
    TLruCache<Object, Object> cache = new TLruCache<>(2);
    Object key = new Object();
    Object value = new Object();

    cache.put(key, value);
    Object returnedObject = cache.get(key);

    assertEquals(value, returnedObject);
    assertEquals(1, cache.hitCount());
  }

  @Test
  public void get_valueNotInCache_shouldReturnNull() {
    TLruCache<Object, Object> cache = new TLruCache<>(2);

    Object obj = cache.get(new Object());

    assertNull(obj);
    assertEquals(1, cache.missCount());
  }

  @Test
  public void trimToSize() {
    TLruCache<Object, Object> cache = new TLruCache<>(4);
    cache.put(new Object(), new Object());
    cache.put(new Object(), new Object());
    cache.put(new Object(), new Object());

    cache.trimToSize(1);

    assertEquals(1, cache.size());
    assertEquals(2, cache.evictionCount());
  }

  @Test
  public void evictTo_itemTooLongInCache_shouldEvict() {
    TLruCache<Object, Object> cache = new TLruCache<>(3);
    cache.put(new Object(), new Object());
    try {
      Thread.sleep(100);
    } catch (InterruptedException exception) {
      exception.printStackTrace();
    }
    cache.put(new Object(), new Object());
    try {
      Thread.sleep(100);
    } catch (InterruptedException exception) {
      exception.printStackTrace();
    }
    cache.put(new Object(), new Object());

    cache.evictTo(90, TimeUnit.MILLISECONDS);

    assertEquals(1, cache.getTimestamps().size());
    assertEquals(1, cache.size());
  }

  @Test
  public void evictTo_itemNotTooLongInCache_shouldNotEvict() {
    TLruCache<Object, Object> cache = new TLruCache<>(3);
    cache.put(new Object(), new Object());
    cache.put(new Object(), new Object());

    cache.evictTo(1, TimeUnit.SECONDS);

    assertEquals(2, cache.size());
  }

  @Test
  public void remove_valueIsInCache_shouldReturnValue() {
    TLruCache<Object, Object> cache = new TLruCache<>(2);
    Object key = new Object();
    Object value = new Object();

    cache.put(key, value);
    Object returnedValue = cache.remove(key);
    Map snapshot = cache.snapshot();

    assertEquals(value, returnedValue);
    assertFalse(cache.getTimestamps().containsValue(key));
    assertFalse(snapshot.containsKey(key));
    assertFalse(snapshot.containsValue(value));
  }

  @Test
  public void remove_valueIsNotInCache_shouldReturnNull() {
    TLruCache<Object, Object> cache = new TLruCache<>(2);

    Object returnedValue = cache.remove(new Object());

    assertNull(returnedValue);
  }

  @Test
  public void remove_shouldInvokeReleaseMethodInsideItemRemovedListener() {
    final Object key = new Object();
    final Object value = new Object();
    SkaldLruCache.CacheItemRemovedListener<Object, Object> cacheItemRemovedListener =
        new SkaldLruCache.CacheItemRemovedListener<Object, Object>() {
          @Override
          public void release(Object keyToRelease, Object valueToRelease) {
            assertSame(keyToRelease, key);
            assertSame(valueToRelease, value);
          }
        };
    TLruCache<Object, Object> cache = new TLruCache<>(2, cacheItemRemovedListener);
    cache.put(key, value);

    cache.remove(key);
  }

  @Test
  public void evictAll() {
    TLruCache<Object, Object> cache = new TLruCache<>(2);
    cache.put(new Object(), new Object());
    cache.put(new Object(), new Object());

    cache.evictAll();
    Map snapshot = cache.snapshot();

    assertEquals(0, snapshot.size());
    assertEquals(0, cache.getTimestamps().size());
    assertEquals(2, cache.evictionCount());
  }
}

