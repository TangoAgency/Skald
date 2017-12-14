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

    public boolean equals(Object object) {return object == this || list.equals(object);}

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

    public int indexOf(Object object) {return list.indexOf(object);}

    public int lastIndexOf(Object object) {return list.lastIndexOf(object);}

    public boolean addAll(int index, Collection<? extends E> collection) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super E> comparator) {
      throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator() {return listIterator(0);}

    public ListIterator<E> listIterator(final int index) {
      return new ListIterator<E>() {
        private final ListIterator<? extends E> iterator
            = list.listIterator(index);

        public boolean hasNext() {return iterator.hasNext();}

        public E next() {return iterator.next();}

        public boolean hasPrevious() {return iterator.hasPrevious();}

        public E previous() {return iterator.previous();}

        public int nextIndex() {return iterator.nextIndex();}

        public int previousIndex() {return iterator.previousIndex();}

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
          iterator.forEachRemaining(action);
        }
      };
    }

    public List<E> subList(int fromIndex, int toIndex) {
      return new UnmodifiableList<>(list.subList(fromIndex, toIndex));
    }
  }

  static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
    private static final long serialVersionUID = 1820017752578914078L;

    final Collection<? extends E> collection;

    UnmodifiableCollection(Collection<? extends E> collection) {
      if (collection == null) {
        throw new NullPointerException();
      }
      this.collection = collection;
    }

    public int size() {return collection.size();}

    public boolean isEmpty() {return collection.isEmpty();}

    public boolean contains(Object object) {return collection.contains(object);}

    public Object[] toArray() {return collection.toArray();}

    public <T> T[] toArray(T[] array) {return collection.toArray(array);}

    public String toString() {return collection.toString();}

    public Iterator<E> iterator() {
      return new Iterator<E>() {
        private final Iterator<? extends E> iterator = collection.iterator();

        public boolean hasNext() {return iterator.hasNext();}

        public E next() {return iterator.next();}

        public void remove() {
          throw new UnsupportedOperationException();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
          iterator.forEachRemaining(action);
        }
      };
    }

    public boolean add(E element) {
      throw new UnsupportedOperationException();
    }

    public boolean remove(Object object) {
      throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> collection) {
      return this.collection.containsAll(collection);
    }

    public boolean addAll(Collection<? extends E> collection) {
      throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> collection) {
      throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> collection) {
      throw new UnsupportedOperationException();
    }

    public void clear() {
      throw new UnsupportedOperationException();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void forEach(Consumer<? super E> action) {
      collection.forEach(action);
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("unchecked")
    @Override
    public Spliterator<E> spliterator() {
      return (Spliterator<E>) collection.spliterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> stream() {
      return (Stream<E>) collection.stream();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressWarnings("unchecked")
    @Override
    public Stream<E> parallelStream() {
      return (Stream<E>) collection.parallelStream();
    }
  }
}
