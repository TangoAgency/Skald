package agency.tango.skald.core;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class SkaldUnmodifiableList<T> extends ArrayList<T> {
  public SkaldUnmodifiableList(
      @NonNull Collection<? extends T> c) {
    super(c);
  }

  @Override
  public void trimToSize() {

  }

  @Override
  public T set(int index, T element) {
    return null;
  }

  @Override
  public boolean add(T t) {
    return false;
  }

  @Override
  public T remove(int index) {
    return null;
  }

  @Override
  public void ensureCapacity(int minCapacity) {

  }

  @Override
  public void sort(Comparator<? super T> c) {

  }

  @Override
  public void replaceAll(UnaryOperator<T> operator) {

  }

  @Override
  public boolean removeIf(Predicate<? super T> filter) {
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
  }

  @Override
  protected void removeRange(int fromIndex, int toIndex) {

  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return false;
  }

  @Override
  public void clear() {

  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public void add(int index, T element) {

  }
}
