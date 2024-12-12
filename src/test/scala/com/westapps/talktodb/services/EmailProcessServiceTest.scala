//package com.westapps.talktodb.services
//
//import com.westapps.talktodb.aws.SesEmailClient
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito._
//import reactor.test.StepVerifier
//import com.westapps.talktodb.domain.EmailForm
//
//class EmailProcessServiceTest {
//
//  @Test
//  def testProcessEmailForm(): Unit = {
//    // Arrange
//    val emailClientMock = mock(classOf[SesEmailClient])
//    val emailProcessService = new EmailProcessService(emailClientMock)
//    val emailForm =
//      EmailForm("Test User", "test@example.com", "This is a test message.")
//
//    // Act
//    val result = emailProcessService.processEmailForm(emailForm)
//
//    // Assert
//    StepVerifier
//      .create(result)
//      .verifyComplete()
//
//    verify(emailClientMock).sendEmail(emailForm, "recipient@example.com")
//  }
//
//}
