package agency.tango.skald.core;

import android.util.Log;

import agency.tango.skald.core.callbacks.SkaldOperationCallback;
import agency.tango.skald.core.exceptions.OperationFailedException;
import io.reactivex.CompletableEmitter;

public class SkaldOperationCallbackImpl implements SkaldOperationCallback {
  private final CompletableEmitter completableEmitter;

  public SkaldOperationCallbackImpl(CompletableEmitter completableEmitter) {
    this.completableEmitter = completableEmitter;
  }

  @Override
  public void onSuccess() {
    Log.d("test", "SkaldCallbackComplete");
    completableEmitter.onComplete();
  }

  @Override
  public void onError() {
    completableEmitter.onError(new OperationFailedException());
  }
}
