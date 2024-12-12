package com.westapps.talktodb.aws

import com.typesafe.scalalogging.LazyLogging
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.internal.async.ByteArrayAsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest

import java.nio.file.Path
import scala.concurrent.ExecutionContext

class S3Client(
  s3AsyncClient: S3AsyncClient
)(implicit val executionContext: ExecutionContext) extends LazyLogging {

  def uploadFile(bucketName: String, filePath: Path, objKey: String): Mono[String] = {
    val putObjectRequest = PutObjectRequest
      .builder()
      .bucket(bucketName)
      .key(objKey)
      .build()

    Mono.fromFuture(s3AsyncClient.putObject(putObjectRequest, filePath)).map(_ => objKey)
  }

  def uploadStr(data: String, bucketName: String, objKey: String): Mono[String] = {
    val putObjectRequest = PutObjectRequest
      .builder()
      .bucket(bucketName)
      .key(objKey)
      .build()

    val asyncRequestBody = AsyncRequestBody.fromString(data)
    Mono.fromFuture(s3AsyncClient.putObject(putObjectRequest, asyncRequestBody)).map(_ => objKey)
  }

  def download(bucketName: String, saveFilePath: Path, fileKey: String): Mono[GetObjectResponse] = {
    val getObjectRequest = GetObjectRequest
      .builder()
      .bucket(bucketName)
      .key(fileKey)
      .build()

    Mono.fromFuture(s3AsyncClient.getObject(getObjectRequest, saveFilePath))
  }

  def download(bucketName: String, s3Key: String): Mono[Array[Byte]] = {
    val request = GetObjectRequest
      .builder()
      .bucket(bucketName)
      .key(s3Key)
      .build()

    Mono
      .fromFuture(s3AsyncClient.getObject(request, new ByteArrayAsyncResponseTransformer[GetObjectResponse]()))
      .map(result => result.asByteArray())
  }
}
