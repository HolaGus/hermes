package gus.hermes.slack.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;

import java.util.function.Supplier;

/**
 * Created by domix on 22/11/15.
 */
public abstract class AbstractCommand<T> extends HystrixObservableCommand<T> {
  public static final String SLACK_COMMAND_GROUP_KEY = "http-slack-api";
  private Supplier<Observable<T>> function;
  private String cacheKey = null;
  private T defaultResult = null;

  public AbstractCommand(String commandKey) {
    super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey)));
  }

  public AbstractCommand(String commandKey, CommandData<T> commandData) {
    super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey)));
    setCommandData(commandData);
  }

  public AbstractCommand(String commandKey, CommandData<T> commandData, Supplier<rx.Observable<T>> function) {
    super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey)));
    this.function = function;
    setCommandData(commandData);
  }

  public AbstractCommand(String commandKey, Integer timeout, CommandData<T> commandData) {
    super(HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
      .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
        .withExecutionTimeoutInMilliseconds(timeout)));
    setCommandData(commandData);
  }

  private void setCommandData(CommandData<T> commandData) {
    this.cacheKey = commandData.getCacheKey();
    this.defaultResult = commandData.getDefaultValue();
  }

  @Override
  protected Observable<T> construct() {
    try {
      return function.get();
    } catch (Exception e) {
      return Observable.error(e);
    }
  }

  @Override
  protected rx.Observable<T> resumeWithFallback() {
    return rx.Observable.just(defaultResult);
  }

  @Override
  protected String getCacheKey() {
    return this.cacheKey;
  }

  public static rx.Observable of() {
    return null;
  }
}
