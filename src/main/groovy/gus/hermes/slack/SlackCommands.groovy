package gus.hermes.slack

import static gus.hermes.slack.commands.CommandData.with
import static ratpack.rx.RxRatpack.observe

import gus.hermes.slack.commands.AbstractCommand
import ratpack.http.client.ReceivedResponse

/**
 * Created by domix on 10/11/15.
 */
class SlackCommands {

  public static final String SLACK_COMMAND_GROUP_KEY = 'http-slack-api'
  private final ratpack.http.client.HttpClient httpClient
  private final ratpack.config.ConfigData configData
  private final String slackApiToken

  @com.google.inject.Inject
  SlackCommands(ratpack.http.client.HttpClient httpClient, ratpack.config.ConfigData configData) {
    this.configData = configData
    this.httpClient = httpClient
    this.slackApiToken = configData.get('/slackApiToken', String)
  }

  rx.Observable<String> getChannels() {
    new AbstractCommand<String>('getChannels', with('http-slack-channels', '{}')) {

      @Override
      protected rx.Observable<String> construct() {
        URI uri = "https://slack.com/api/channels.list?token=${slackApiToken}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }
    }.toObservable()
  }

  rx.Observable<String> getChannelsInfo(final String channel) {
    new AbstractCommand<String>('getChannelsInfo', with("http-slack-channels-${channel}", '{}')) {

      @Override
      protected rx.Observable<String> construct() {
        URI uri = "https://slack.com/api/channels.info?token=${slackApiToken}&channel=${channel}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }
    }.toObservable()
  }

  rx.Observable<String> getUsers() {
    new AbstractCommand<String>('getUsers', with('{}')) {
      @Override
      protected rx.Observable<String> construct() {
        URI uri = "https://slack.com/api/users.list?token=${slackApiToken}&presence=1".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }
    }.toObservable()
  }

  rx.Observable<String> getUsersInfo(final String user) {
    new AbstractCommand<String>('getUsersInfo', with('{}')) {

      @Override
      protected rx.Observable<String> construct() {
        URI uri = "https://slack.com/api/users.info?token=${slackApiToken}&user=${user}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

    }.toObservable()
  }

  rx.Observable<String> getChannelsHistoryNew(final String channel) {
    new AbstractCommand('getChannelsHistory', 3000, with("http-slack-channels-info-${channel}", '{}')) {

      @Override
      protected rx.Observable<String> construct() {
        URI uri = "https://slack.com/api/channels.history?token=${slackApiToken}&channel=${channel}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          println "resp.statusCode: ${resp.statusCode}"
          resp.body.text
        }
      }
    }.toObservable()
  }
}