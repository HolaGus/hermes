package gus.hermes.slack

import static ratpack.rx.RxRatpack.observe

import com.google.inject.Inject
import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixObservableCommand
import ratpack.config.ConfigData
import ratpack.http.client.HttpClient
import ratpack.http.client.ReceivedResponse
import rx.Observable

/**
 * Created by domix on 10/11/15.
 */
class SlackCommands {

  public static final String SLACK_COMMAND_GROUP_KEY = 'http-slack-api'
  private final HttpClient httpClient
  private final ConfigData configData

  @Inject
  SlackCommands(HttpClient httpClient, ConfigData configData) {
    this.configData = configData
    this.httpClient = httpClient
  }

  Observable<String> getChannels() {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getChannels'))) {

      @Override
      protected Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/channels.list?token=${slackApiToken}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected Observable<String> resumeWithFallback() {
        Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        'http-slack-channels'
      }
    }.toObservable()
  }

  Observable<String> getChannelsInfo(final String channel) {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getChannelsInfo'))) {

      @Override
      protected Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/channels.info?token=${slackApiToken}&channel=${channel}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected Observable<String> resumeWithFallback() {
        Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        "http-slack-channels-${channel}"
      }
    }.toObservable()
  }

  Observable<String> getUsers() {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getUsers'))) {

      @Override
      protected Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/users.list?token=${slackApiToken}&presence=1".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected Observable<String> resumeWithFallback() {
        Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        'http-slack-users'
      }
    }.toObservable()
  }

  Observable<String> getUsersInfo(final String user) {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getUsersInfo'))) {

      @Override
      protected Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/users.info?token=${slackApiToken}&user=${user}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected Observable<String> resumeWithFallback() {
        Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        "http-slack-users-info-${user}"
      }
    }.toObservable()
  }
}
