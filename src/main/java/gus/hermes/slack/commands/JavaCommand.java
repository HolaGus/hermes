package gus.hermes.slack.commands;

import com.google.inject.Inject;
import ratpack.config.ConfigData;
import ratpack.http.client.HttpClient;

/**
 * Created by domix on 22/11/15.
 */
public class JavaCommand {

  public static final String SLACK_COMMAND_GROUP_KEY = "http-slack-api";
  private final HttpClient httpClient;
  private final ConfigData configData;

  @Inject
  public JavaCommand(HttpClient httpClient, ConfigData configData) {
    this.configData = configData;
    this.httpClient = httpClient;
  }
  /**
   *
   * HystrixUtils.observableCommand(SLACK_COMMAND_GROUP_KEY, 'getChannelsHistory', '{}', 1000) {
   String slackApiToken = configData.get('/slackApiToken', String)
   println "slackApiToken: ${slackApiToken}"
   URI uri = "https://slack.com/api/channels.history?token=${slackApiToken}&channel=${channel}".toURI()
   println "URI: ${uri.toString()}"
   println "httpclient: ${httpClient}"
   observe(httpClient.get(uri)).map { ReceivedResponse resp ->
   println "status: ${resp.status.code}"

   String text = resp.body.text
   println "resp: ${text}"

   return text
   }
   }
   */


}
