package gus.hermes.slack.commands;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by domix on 22/11/15.
 */
@Builder
@Getter
public class CommandData<T> {
  private String cacheKey = null;
  private T defaultValue = null;

  public static CommandData with(String cacheKey) {
    return builder().cacheKey(cacheKey).build();
  }

  public static CommandData with(String cacheKey, Object defaultValue) {
    return builder().cacheKey(cacheKey).defaultValue(defaultValue).build();
  }

  public static CommandData with(Object defaultValue) {
    return builder().defaultValue(defaultValue).build();
  }
}
