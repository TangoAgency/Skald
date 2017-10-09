package agency.tango.skald.core.bus;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SkaldBus {
  private static volatile SkaldBus instance = null;

  public static SkaldBus getInstance() {
    if (instance == null) {
      instance = new SkaldBus();
    }
    return instance;
  }

  private final PublishSubject<SkaldEvent> eventPublishSubject = PublishSubject.create();

  public void post(SkaldEvent event) {
    eventPublishSubject.onNext(event);
  }

  public Subject<SkaldEvent> onSkaldEvent() {
    return eventPublishSubject.toSerialized();
  }
}
