package com.westapps.talktodb

import com.westapps.talktodb.restful.controllers.IndexController
import com.westapps.talktodb.spec.ActiveProfileSpec
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = Array(classOf[Application]))
class ApplicationTest extends ActiveProfileSpec {
  @Autowired var indexController: IndexController = _

  @Test
  def contextLoaded(): Unit = {
    Assertions.assertThat(indexController).isNotNull
  }
}
