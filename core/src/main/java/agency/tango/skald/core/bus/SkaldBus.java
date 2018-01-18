package agency.tango.skald.core.bus;

import io.reactivex.Observable;
import android.support.annotation.NonNull;
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
    if (bus.hasObservers()) {
      bus.onNext(event);
    }
  }

  public <T> Observable<T> observable(@NonNull final Class<T> eventClass) {
    return bus
        .filter(object -> objectInstanceOf(eventClass, object))
        .cast(eventClass);
  }

  private <T> boolean objectInstanceOf(@NonNull Class<T> eventClass, Object object) {
    return object != null && eventClass.isInstance(object);
  }
}
