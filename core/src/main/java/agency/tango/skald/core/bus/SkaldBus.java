package agency.tango.skald.core.bus;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SkaldBus {
  private static volatile SkaldBus instance;
  private final Subject<Object> bus = PublishSubject.create().toSerialized();

  public static SkaldBus getInstance() {
    if (instance == null) {
      instance = new SkaldBus();
    }
    return instance;
  }

  public void post(@NonNull Object event) {
    if(bus.hasObservers()) {
      bus.onNext(event);
    }
  }

  public <T> Observable<T> observable(@NonNull final Class<T> eventClass) {
    return bus
        .filter(new Predicate<Object>() {
          @Override
          public boolean test(@NonNull Object o) throws Exception {
            return o != null;
          }
        })
        .filter(new Predicate<Object>() {
          @Override
          public boolean test(@NonNull Object o) throws Exception {
            return eventClass.isInstance(o);
          }
        })
        .cast(eventClass);
  }
}
