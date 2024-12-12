//package com.westapps.talktodb.aws
//
//import com.westapps.talktodb.LocalMainApplication
//import com.westapps.talktodb.configs.AppConfig
//import com.westapps.talktodb.configs.AwsConfig
//import com.westapps.talktodb.configs.IntegrationConfig
//import com.westapps.talktodb.spec.ActiveProfileSpec
//import org.junit.jupiter.api.Assertions._
//import org.junit.jupiter.api._
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import org.springframework.test.annotation.DirtiesContext
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//import software.amazon.awssdk.services.ses.SesAsyncClient
//import software.amazon.awssdk.services.ses.model.SendEmailResponse
//import software.amazon.awssdk.services.ses.model.SendRawEmailResponse
//
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//@SpringBootTest(classes = Array(classOf[LocalMainApplication]))
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Import(value = Array(classOf[AppConfig], classOf[IntegrationConfig], classOf[AwsConfig]))
//class EmailClientIntegrationTest extends ActiveProfileSpec {
//
//  @Autowired var appConfig: AppConfig = _
//  @Autowired var awsConfig: AwsConfig = _
//  @Autowired var sesAsyncClient: SesAsyncClient = _
//
//  var emailClient: SesEmailClient = _
//
//  private val testRecipient = "simon.fuxi.xie@gmail.com"
//  private val testSubject = "Integration Test Email"
//  private val testBodyHTML = "<h1>This is a test email</h1><p>Sent during integration testing.</p>"
//  private val attachmentFileName = "test.txt"
//  private val attachmentContent = "This is a test attachment.".getBytes("UTF-8")
//  private val attachmentContentType = "text/plain"
//
//  @BeforeAll
//  def setup(): Unit = {
//    emailClient = new SesEmailClient(
//      sesAsyncClient,
//      awsConfig.ses.senderEmail,
//      awsConfig.ses.replyToEmail,
//      appConfig.name
//    )
//  }
//
//  @Test
//  @DisplayName("Test sendEmail method")
//  def testSendEmail(): Unit = {
//    val responseMono: Mono[SendEmailResponse] = emailClient.sendEmail(
//      recipient = testRecipient,
//      subject = testSubject,
//      bodyHTML = testBodyHTML
//    )
//
//    StepVerifier.create(responseMono)
//      .assertNext(response => {
//        assertNotNull(response)
//        assertTrue(response.messageId().nonEmpty, "Message ID should not be empty")
//      })
//      .verifyComplete()
//  }
//
//  @Test
//  @DisplayName("Test sendEmailWithAttachment method")
//  def testSendEmailWithAttachment(): Unit = {
//    val responseMono: Mono[SendRawEmailResponse] = emailClient.sendEmailWithAttachment(
//      recipient = testRecipient,
//      subject = testSubject,
//      bodyHTML = testBodyHTML,
//      attachmentFileName = attachmentFileName,
//      attachmentByteArray = attachmentContent,
//      attachmentContentType = attachmentContentType
//    )
//
//    StepVerifier.create(responseMono)
//      .assertNext(response => {
//        assertNotNull(response)
//        assertTrue(response.messageId().nonEmpty, "Message ID should not be empty")
//      })
//      .verifyComplete()
//  }
//
//  @AfterAll
//  def teardown(): Unit = {
//    if (sesAsyncClient != null) {
//      sesAsyncClient.close()
//    }
//  }
//}