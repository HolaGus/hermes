package gus.hermes.slack

import static ratpack.rx.RxRatpack.observe

import com.netflix.hystrix.HystrixCommandGroupKey
import com.netflix.hystrix.HystrixCommandKey
import com.netflix.hystrix.HystrixObservableCommand
import gus.hermes.slack.commands.HystrixUtils
import ratpack.exec.Blocking
import ratpack.exec.Promise
import ratpack.http.client.ReceivedResponse

/**
 * Created by domix on 10/11/15.
 */
class SlackCommands {

  public static final String SLACK_COMMAND_GROUP_KEY = 'http-slack-api'
  private final ratpack.http.client.HttpClient httpClient
  private final ratpack.config.ConfigData configData

  @com.google.inject.Inject
  SlackCommands(ratpack.http.client.HttpClient httpClient, ratpack.config.ConfigData configData) {
    this.configData = configData
    this.httpClient = httpClient
  }

  rx.Observable<String> getChannels() {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getChannels'))) {

      @Override
      protected rx.Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/channels.list?token=${slackApiToken}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected rx.Observable<String> resumeWithFallback() {
        rx.Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        'http-slack-channels'
      }
    }.toObservable()
  }

  rx.Observable<String> getChannelsInfo(final String channel) {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getChannelsInfo'))) {

      @Override
      protected rx.Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/channels.info?token=${slackApiToken}&channel=${channel}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected rx.Observable<String> resumeWithFallback() {
        rx.Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        "http-slack-channels-${channel}"
      }
    }.toObservable()
  }

  rx.Observable<String> getUsers() {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getUsers'))) {

      @Override
      protected rx.Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/users.list?token=${slackApiToken}&presence=1".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected rx.Observable<String> resumeWithFallback() {
        rx.Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        'http-slack-users'
      }
    }.toObservable()
  }

  rx.Observable<String> getUsersInfo(final String user) {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getUsersInfo'))) {

      @Override
      protected rx.Observable<String> construct() {
        String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/users.info?token=${slackApiToken}&user=${user}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }
      }

      @Override
      protected rx.Observable<String> resumeWithFallback() {
        rx.Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        "http-slack-users-info-${user}"
      }
    }.toObservable()
  }

  rx.Observable<String> getChannelsHistory(final String channel) {
    HystrixUtils.observableCommand(SLACK_COMMAND_GROUP_KEY, 'getChannelsHistory', '{}', 3000) {
      String slackApiToken = configData.get('/slackApiToken', String)
      println "slackApiToken: ${slackApiToken}"
      URI uri = "https://slack.com/api/channels.history?token=${slackApiToken}&channel=${channel}".toURI()
      println "URI: ${uri.toString()}"
      println "httpclient: ${httpClient}"

      observe(Blocking.get { httpClient.get(uri) }).map { ReceivedResponse resp ->
        println "status: ${resp.status.code}"

        String text = resp.body.text
        println "resp: ${text}"

        return text
      }
    }
  }

  rx.Observable<String> getChannelsHistoryNew(final String channel) {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SLACK_COMMAND_GROUP_KEY))
        .andCommandKey(HystrixCommandKey.Factory.asKey('getChannelsHistory'))) {

      @Override
      protected rx.Observable<String> construct() {
        /*String slackApiToken = configData.get('/slackApiToken', String)
        URI uri = "https://slack.com/api/channels.history?token=${slackApiToken}&channel=${channel}".toURI()
        observe(httpClient.get(uri)).map { ReceivedResponse resp ->
          resp.body.text
        }*/
        observe(Blocking.get {
          String slackApiToken = configData.get('/slackApiToken', String)
          URI uri = "https://slack.com/api/channels.history?token=${slackApiToken}&channel=${channel}".toURI()
          def get = httpClient.get(uri).map {
            it.body.text
          }

          '{ "success": true }'
          //response.body.text
          '{ "success": true }'
        })
      }

      @Override
      protected rx.Observable<String> resumeWithFallback() {
        rx.Observable.just('{}')
      }

      @Override
      protected String getCacheKey() {
        "http-slack-channels-${channel}"
      }
    }.toObservable()
  }
}