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

  private final HttpClient httpClient
  private final ConfigData configData

  @Inject
  SlackCommands(HttpClient httpClient, ConfigData configData) {
    this.configData = configData
    this.httpClient = httpClient
  }

  Observable<String> getChannels() {
    new HystrixObservableCommand<String>(
      HystrixObservableCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey('http-slack-api'))
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
}
