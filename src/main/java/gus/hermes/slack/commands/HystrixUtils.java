package gus.hermes.slack.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.HystrixObservableCommand.Setter;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import rx.Observable;

import java.util.function.Supplier;

/**
 * Created by domix on 22/11/15.
 */
public class HystrixUtils {
  public static <T> Observable<T> observableCommand(String groupKey, String commandKey, T defaultResult, int timeout, Supplier<Observable<T>> function) {
    return new HystrixObservableCommand<T>(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionTimeoutInMilliseconds(timeout))) {
      @Override
      protected Observable<T> construct() {
        try {
          return function.get();
        } catch (Exception e) {
          return Observable.error(e);
        }
      }
      @Override
      protected Observable<T> resumeWithFallback() {
        handleErrors();
        return Observable.just(defaultResult);
      }
      protected void handleErrors() {
        final String message;
        if (isFailedExecution()) {
          message = "FAILED: " + getFailedExecutionException().getMessage();
        } else if (isResponseTimedOut()) {
          message = "TIMED OUT: " + getExecutionTimeInMilliseconds() +
            ", " + getProperties().executionTimeoutInMilliseconds().get();
        } else {
          message = "SOME OTHER FAILURE";
        }
        System.out.println("ERROR: "+message);
      }
    }.observe().onErrorResumeNext(e -> {
      if (e instanceof HystrixRuntimeException) {
        HystrixRuntimeException he = (HystrixRuntimeException) e;

        System.out.println(he.getFailureType());
        return Observable.error(he.getCause());
      }
      return Observable.error(e);
    });
  }
}
