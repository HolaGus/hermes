package gus.hermes.slack.commands

import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixCommandProperties
import com.netflix.hystrix.HystrixObservableCommand
import com.netflix.hystrix.HystrixObservableCommand.Setter
import com.netflix.hystrix.exception.HystrixRuntimeException
import rx.Observable

import java.util.function.Supplier

/**
 * Created by domix on 22/11/15.
 */
class HystrixCommandsGroovy {
  public static <T> Observable<T> hystrixed(String groupKey, String commandKey, int timeout, Supplier<Observable<T>> function) {
    return new HystrixObservableCommand(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
      .withExecutionTimeoutInMilliseconds(timeout))) {
      @Override
      protected Observable construct() {
        try {
          return function.get();
        } catch (Exception e) {
          return Observable.error(e);
        }
      }

    }.observe().onErrorResumeNext({ e ->
      if (e instanceof HystrixRuntimeException) {
        return Observable.error(((HystrixRuntimeException) e).getCause());
      }
      return Observable.error(e);
    });
  }
}
