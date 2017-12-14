package agency.tango.skald.core;

import android.os.Build;
import android.support.annotation.RequiresApi;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class CollectionsCompat {

  public static <T> List<T> unmodifiableList(List<T> list) {
    return new UnmodifiableList<>(list);
  }

  static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
    private static final long serialVersionUID = -283967356065247728L;

    final List<? extends E> list;

    UnmodifiableList(List<? extends E> list) {
      super(list);
      this.list = list;
    }

    public boolean equals(Object o) {return o == this || list.equals(o);}

    public int hashCode() {return list.hashCode();}

    public E get(int index) {return list.get(index);}

    public E set(int index, E element) {
      throw new UnsupportedOperationException();
    }

    public void add(int index, E element) {
      throw new UnsupportedOperationException();
    }

    public E remove(int index) {
      throw new UnsupportedOperationException();
    }

    public int indexOf(Object o) {return list.indexOf(o);}

    public int lastIndexOf(Object o) {return list.lastIndexOf(o);}

    public boolean addAll(int index, Collection<? extends E> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super E> c) {
      throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator() {return listIterator(0);}

    public ListIterator<E> listIterator(final int index) {
      return new ListIterator<E>() {
        private final ListIterator<? extends E> i
            = list.listIterator(index);

        public boolean hasNext() {return i.hasNext();}

        public E next() {return i.next();}

        public boolean hasPrevious() {return i.hasPrevious();}

        public E previous() {return i.previous();}

        public int nextIndex() {return i.nextIndex();}

        public int previousIndex() {return i.previousIndex();}

        public void remove() {
          throw new UnsupportedOperationException();
        }

        public void set(E e) {
          throw new UnsupportedOperationException();
        }

        public void add(E e) {
          throw new UnsupportedOperationException();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
          i.forEachRemaining(action);
        }
      };
    }

    public List<E> subList(int fromIndex, int toIndex) {
      return new UnmodifiableList<>(list.subList(fromIndex, toIndex));
    }
  }

  static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
    private static final long serialVersionUID = 1820017752578914078L;

    final Collection<? extends E> c;

    UnmodifiableCollection(Collection<? extends E> c) {
      if (c == null) {
        throw new NullPointerException();
      }
      this.c = c;
    }

    public int size() {return c.size();}

    public boolean isEmpty() {return c.isEmpty();}

    public boolean contains(Object o) {return c.contains(o);}

    public Object[] toArray() {return c.toArray();}

    public <T> T[] toArray(T[] a) {return c.toArray(a);}

    public String toString() {return c.toString();}

    public Iterator<E> iterator() {
      return new Iterator<E>() {
        private final Iterator<? extends E> i = c.iterator();

        public boolean hasNext() {return i.hasNext();}

        public E next() {return i.next();}

        public void remove() {
          throw new UnsupportedOperationException();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
          i.forEachRemaining(action);
        }
      };
    }

    public boolean add(E e) {
      throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
      throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> coll) {
      return c.containsAll(coll);
    }

    public boolean addAll(Collection<? extends E> coll) {
      throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> coll) {
      throw new UnsupportedOperationException();
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void forEach(Consumer<? super E> action) {
      c.forEach(action);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("unchecked")
    @Override
    public Spliterator<E> spliterator() {
      return (Spliterator<E>) c.spliterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> stream() {
      return (Stream<E>) c.stream();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> parallelStream() {
      return (Stream<E>) c.parallelStream();
    }
  }
}
