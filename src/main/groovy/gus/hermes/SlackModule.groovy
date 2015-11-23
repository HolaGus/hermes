package gus.hermes

import static com.google.inject.Scopes.SINGLETON

import com.google.inject.AbstractModule
import gus.hermes.api.HermesRestEndpoint
import gus.hermes.slack.SlackCommands
import gus.hermes.slack.commands.JavaCommand

/**
 * Created by domix on 10/11/15.
 */
class SlackModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SlackCommands).in(SINGLETON)
    bind(JavaCommand).in(SINGLETON)
    bind(HermesRestEndpoint).in(SINGLETON)
  }
}
