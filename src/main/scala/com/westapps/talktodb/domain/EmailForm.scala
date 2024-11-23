package com.westapps.talktodb.domain

case class EmailForm(name: String, email: String, message: String) {
  def toBodyHTML: String = {
    s"""
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>Message from $name</title>
    </head>
    <body>
        <h2>Contact Form Submission</h2>
        <p><strong>Name:</strong> $name</p>
        <p><strong>Email:</strong> $email</p>
        <p><strong>Message:</strong></p>
        <p>${message.replaceAll("\n", "<br/>")}</p>
    </body>
    </html>
    """
  }
}
