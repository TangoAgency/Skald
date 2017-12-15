package agency.tango.skald.deezer.services.listeners;

import com.deezer.sdk.network.request.event.JsonRequestListener;
import agency.tango.skald.deezer.exceptions.UnparsedResultException;
import io.reactivex.SingleEmitter;

public abstract class DeezerRequestListener<T> extends JsonRequestListener {
  private final SingleEmitter<T> emitter;

  protected DeezerRequestListener(SingleEmitter<T> emitter) {
    this.emitter = emitter;
  }

  @Override
  public void onUnparsedResult(String requestResponse, Object requestId) {
    emitter.onError(new UnparsedResultException(
        String.format("Unparsed result from request response : %s", requestResponse)));
  }

  @Override
  public void onException(Exception exception, Object requestId) {
    emitter.onError(exception);
  }
}
