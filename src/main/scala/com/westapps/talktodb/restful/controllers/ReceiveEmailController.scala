package com.westapps.talktodb.restful.controllers

import com.typesafe.scalalogging.LazyLogging
import com.westapps.talktodb.domain.EmailForm
import com.westapps.talktodb.restful.controllers.ReceiveEmailController.EmailFormDTO
import com.westapps.talktodb.services.EmailProcessService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation._
import reactor.core.publisher.Mono

import scala.beans.BeanProperty

@RestController
@RequestMapping(Array("/api/v1/aws"))
class ReceiveEmailController @Autowired()(
  private val emailProcessService: EmailProcessService
) extends LazyLogging {

  @PostMapping(Array("/send"))
  def receiveEmail(
    @RequestParam source: String,
    @Valid @RequestBody emailFormDTO: EmailFormDTO
  ): Mono[ResponseEntity[String]] = {
    val emailForm = emailFormDTO.toEmailForm()
    val recipient = "simon.fuxi.xie@gmail.com"
    emailProcessService
      .processEmailForm(emailForm, recipient, source)
      .map { result: Boolean =>
        if (result) {
          ResponseEntity.ok("Email sent successfully")
        } else {
          ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email")
        }
      }
  }
}

object ReceiveEmailController {
  case class EmailFormDTO(
    name: String,

    @NotBlank
    @Email(message = "Email should be valid")
    @BeanProperty
    email: String,

    @NotBlank
    @BeanProperty
    message: String
  ) {
    def toEmailForm(): EmailForm = {
      EmailForm(name, email, message)
    }
  }
}
