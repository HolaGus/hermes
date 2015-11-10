import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.jsonNode
import static ratpack.rx.RxRatpack.observe

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ratpack.form.Form
import ratpack.groovy.template.MarkupTemplateModule
import ratpack.rx.RxRatpack
import ratpack.server.Service
import ratpack.server.StartEvent

final Logger logger = LoggerFactory.getLogger(ratpack.class)

ratpack {
  bindings {
    module MarkupTemplateModule

    bindInstance Service, new Service() {
      @Override
      void onStart(StartEvent event) throws Exception {
        logger.info "Initializing RX"
        RxRatpack.initialize()
      }
    }
  }

  handlers {
    get {
      render groovyMarkupTemplate("index.gtpl", title: "My Ratpack App CHIDA")
    }

    path("create") {
      byMethod {
        post {
          //observe(parse(Form)).map { it.get('foo') }.subscribe { context.render(it) }
          //parse(jsonNode()).observe()
          //observe(parse(jsonNode())).flatMap().single()
          observe(parse(jsonNode())).map { it.get('foo') }.subscribe { context.render(it.toString()) }
        }
      }
    }

    files { dir "public" }
  }
}
