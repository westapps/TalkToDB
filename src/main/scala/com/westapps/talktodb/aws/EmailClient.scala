package com.westapps.talktodb.aws

import jakarta.activation.DataHandler
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.model.Body
import software.amazon.awssdk.services.ses.model.Content
import software.amazon.awssdk.services.ses.model.Destination
import software.amazon.awssdk.services.ses.model.Message
import software.amazon.awssdk.services.ses.model.RawMessage
import software.amazon.awssdk.services.ses.model.SendEmailRequest
import software.amazon.awssdk.services.ses.model.SendEmailResponse
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest
import software.amazon.awssdk.services.ses.model.SendRawEmailResponse

import java.io.ByteArrayOutputStream
import java.util.Properties
import scala.collection.convert.AsScalaExtensions


class EmailClient (
  @Autowired private val sesAsyncClient: SesAsyncClient,
  private val senderEmail: String,
  private val replyToEmail: String,
  private val appName: String
) extends AsScalaExtensions {
  private val senderEmailWithName = s"${appName} Notification <$senderEmail>"

  def sendEmail(
    recipient: String,
    subject: String,
    bodyHTML: String
  ): Mono[SendEmailResponse] = {
    val destination = Destination.builder().toAddresses(recipient).build()
    val content = Content.builder().data(bodyHTML).build()
    val body = Body.builder().html(content).build()
    val sub = Content.builder().data(subject).build()
    val msg = Message.builder().subject(sub).body(body).build()

    val request = SendEmailRequest
      .builder()
      .destination(destination)
      .message(msg)
      .source(senderEmail)
      .replyToAddresses(replyToEmail)
      .build()

    val sendEmailResponse: Mono[SendEmailResponse] = Mono.fromCompletionStage(sesAsyncClient.sendEmail(request))

    sendEmailResponse
  }

  def sendEmailWithAttachment(
    recipient: String,
    subject: String,
    bodyHTML: String,
    attachmentFileName: String,
    attachmentByteArray: Array[Byte],
    attachmentContentType: String,
  ): Mono[SendRawEmailResponse] = {
    val session: Session = Session.getInstance(new Properties(), null)
    val mimeMessage = new MimeMessage(session)

    mimeMessage.setFrom(senderEmailWithName)
    mimeMessage.setSubject(subject)
    mimeMessage.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(recipient))
    mimeMessage.setReplyTo(Array(new InternetAddress(replyToEmail)))

    val mimeMultipart = new MimeMultipart("mixed")
    val body = new MimeBodyPart()
    body.setContent(bodyHTML, "text/html")
    mimeMultipart.addBodyPart(body)

    val mimeBodyPart = new MimeBodyPart()
    val dataSourceAttach = new ByteArrayDataSource(attachmentByteArray, attachmentContentType)
    dataSourceAttach.setName(attachmentFileName)
    mimeBodyPart.setDataHandler(new DataHandler(dataSourceAttach))
    mimeBodyPart.setFileName(dataSourceAttach.getName)

    mimeMultipart.addBodyPart(mimeBodyPart)
    mimeMessage.setContent(mimeMultipart)

    val outputStream = new ByteArrayOutputStream()
    try {
      mimeMessage.writeTo(outputStream)
      val rawMessage = RawMessage.builder.data(SdkBytes.fromByteArray(outputStream.toByteArray)).build()
      val sendRawEmailRequest = SendRawEmailRequest.builder().rawMessage(rawMessage).build()

      Mono.fromCompletionStage(sesAsyncClient.sendRawEmail(sendRawEmailRequest))
    } finally {
      IOUtils.closeQuietly(outputStream)
    }
  }
}
