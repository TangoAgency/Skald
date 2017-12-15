package agency.tango.skald.core.callbacks;

import io.reactivex.CompletableEmitter;

public class SkaldCoreOperationCallback implements SkaldOperationCallback {
  private final CompletableEmitter completableEmitter;

  public SkaldCoreOperationCallback(CompletableEmitter completableEmitter) {
    this.completableEmitter = completableEmitter;
  }

  @Override
  public void onSuccess() {
    completableEmitter.onComplete();
  }

  @Override
  public void onError(Exception exception) {
    completableEmitter.onError(exception);
  }
}
