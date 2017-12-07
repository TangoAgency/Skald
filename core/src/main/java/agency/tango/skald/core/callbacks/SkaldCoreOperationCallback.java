package agency.tango.skald.core.callbacks;

import agency.tango.skald.core.exceptions.OperationFailedException;
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
  public void onError() {
    completableEmitter.onError(new OperationFailedException());
  }
}
