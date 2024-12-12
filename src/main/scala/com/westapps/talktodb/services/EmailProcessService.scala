package com.westapps.talktodb.services

import com.westapps.talktodb.aws.SesEmailClient
import com.westapps.talktodb.domain.EmailForm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import software.amazon.awssdk.services.ses.model.SendEmailResponse

@Service
class EmailProcessService @Autowired()(
  private val emailClient: SesEmailClient
) {
  def processEmailForm(emailForm: EmailForm, recipientEmail: String, source: String): Mono[Boolean] = {
    val subject = source
    val bodyHTML = emailForm.toBodyHTML
    val monoResponse = emailClient.sendEmail(recipient = recipientEmail, subject = subject, bodyHTML = bodyHTML)
    monoResponse.map { res: SendEmailResponse =>
      if (res.sdkHttpResponse().isSuccessful) true else false
    }
  }
}
