//package com.westapps.talktodb.restful.controllers
//
//import org.junit.jupiter.api.Test
//import org.mockito.Mockito._
//import org.springframework.http.ResponseEntity
//import org.springframework.http.HttpStatus
//import reactor.core.publisher.Mono
//import reactor.test.StepVerifier
//import com.westapps.talktodb.services.EmailProcessService
//import com.westapps.talktodb.domain.EmailForm
//import com.westapps.talktodb.restful.controllers.ReceiveEmailController.EmailFormDTO
//import org.mockito.ArgumentMatchers.any
//
//class ReceiveEmailControllerTest {
//
//  @Test
//  def testReceiveEmail_Success(): Unit = {
//    // Arrange
//    val emailProcessServiceMock = mock(classOf[EmailProcessService])
//    when(emailProcessServiceMock.processEmailForm(any(classOf[EmailForm])))
//      .thenReturn(Mono.empty[Unit]())
//
//    val controller = new ReceiveEmailController(emailProcessServiceMock)
//
//    val emailFormDTO =
//      EmailFormDTO("Test User", "test@example.com", "This is a test message.")
//
//    // Act
//    val responseMono = controller.receiveEmail(emailFormDTO)
//
//    // Assert
//    StepVerifier
//      .create(responseMono)
//      .expectNextMatches(
//        (response: ResponseEntity[String]) =>
//          response.getStatusCode == HttpStatus.OK &&
//            response.getBody == "Email sent successfully"
//      )
//      .verifyComplete()
//
//    verify(emailProcessServiceMock).processEmailForm(
//      EmailForm(
//        emailFormDTO.senderName,
//        emailFormDTO.senderEmail,
//        emailFormDTO.message
//      )
//    )
//  }
//
//  @Test
//  def testReceiveEmail_Failure(): Unit = {
//    // Arrange
//    val emailProcessServiceMock = mock(classOf[EmailProcessService])
//    when(emailProcessServiceMock.processEmailForm(any(classOf[EmailForm])))
//      .thenReturn(Mono.error(new RuntimeException("Failed to send aws")))
//
//    val controller = new ReceiveEmailController(emailProcessServiceMock)
//
//    val emailFormDTO =
//      EmailFormDTO("Test User", "test@example.com", "This is a test message.")
//
//    // Act
//    val responseMono = controller.receiveEmail(emailFormDTO)
//
//    // Assert
//    StepVerifier
//      .create(responseMono)
//      .expectNextMatches(
//        (response: ResponseEntity[String]) =>
//          response.getStatusCode == HttpStatus.INTERNAL_SERVER_ERROR &&
//            response.getBody == "Failed to send aws"
//      )
//      .verifyComplete()
//
//    verify(emailProcessServiceMock).processEmailForm(
//      EmailForm(
//        emailFormDTO.senderName,
//        emailFormDTO.senderEmail,
//        emailFormDTO.message
//      )
//    )
//  }
//
//}
