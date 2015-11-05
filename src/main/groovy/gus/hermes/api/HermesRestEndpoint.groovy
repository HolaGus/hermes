package gus.hermes.api

import static ratpack.jackson.Jackson.json

import com.google.inject.Inject
import groovy.json.JsonSlurper
import gus.hermes.slack.SlackCommands
import ratpack.groovy.handling.GroovyChainAction

/**
 * Created by domix on 10/11/15.
 */
class HermesRestEndpoint extends GroovyChainAction {

  private final SlackCommands slackCommands

  @Inject
  HermesRestEndpoint(SlackCommands slackCommands) {

    this.slackCommands = slackCommands
  }

  @Override
  void execute() throws Exception {
    path("channels") {
      byMethod {
        get {
          slackCommands.channels.single().subscribe {
            render json(new JsonSlurper().parseText(it))
          }
        }
      }
    }
  }
}
