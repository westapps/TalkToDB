package com.westapps.talktodb.services

import com.westapps.talktodb.LocalMainApplication
import com.westapps.talktodb.configs.AppConfig
import com.westapps.talktodb.configs.AwsConfig
import com.westapps.talktodb.configs.IntegrationConfig
import com.westapps.talktodb.domain.EmailForm
import com.westapps.talktodb.spec.ActiveProfileSpec
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api._
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import reactor.test.StepVerifier

@SpringBootTest(classes = Array(classOf[LocalMainApplication]))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(Array(classOf[AppConfig], classOf[IntegrationConfig], classOf[AwsConfig]))
class EmailProcessServiceIntegrationTest extends ActiveProfileSpec {

  @Autowired
  var emailProcessService: EmailProcessService = _

  @Autowired
  var appConfig: AppConfig = _

  @Autowired
  var awsConfig: AwsConfig = _

  private val testRecipient = "simon.fuxi.xie@gmail.com"
  private val source = "Simon Resume"
  private val testEmailForm = EmailForm(
    name = "Test User",
    email = "user@example.com",
    message = "This is a test message."
  )

  @BeforeAll
  def setup(): Unit = {}

  @Test
  @DisplayName("Test processEmailForm method")
  def testProcessEmailForm(): Unit = {
    val resultMono = emailProcessService.processEmailForm(
      emailForm = testEmailForm,
      recipientEmail = testRecipient,
      source = source
    )

    StepVerifier.create(resultMono)
      .assertNext(result => {
        assertTrue(result, "The email should be sent successfully")
      })
      .verifyComplete()
  }
}