package com.example.simpleWebServer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class SimpleWebServerApplication

fun main(args: Array<String>) {
    runApplication<SimpleWebServerApplication>(*args)
}

