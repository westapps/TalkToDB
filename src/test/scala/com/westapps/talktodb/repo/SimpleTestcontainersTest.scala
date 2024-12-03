//package com.westapps.talktodb.repo
//
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
//import org.testcontainers.DockerClientFactory
//import org.testcontainers.containers.GenericContainer
//import org.testcontainers.junit.jupiter.Container
//import org.testcontainers.junit.jupiter.Testcontainers
//
//@Testcontainers
//class SimpleTestcontainersTest {
//  @Container
//  val container: GenericContainer[_] = new GenericContainer("alpine:3.14").withCommand("sleep", "60")
//
//  @Test
//  def testContainerStarts(): Unit = {
//    DockerClientFactory.instance.client
//
//    assertTrue(container.isRunning())
//  }
//}
