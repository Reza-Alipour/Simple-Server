package com.example.simpleWebServer.controller

import com.example.simpleWebServer.service.VideoStreamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.io.IOException


@RestController
@RequestMapping("stream")
class StreamController {

    @Autowired
    private lateinit var videoStreamService: VideoStreamService

    @GetMapping("/{user}/{fileName}.{fileType}")
    fun streamVideo(
        @RequestHeader(value = "Range", required = false) httpRangeList: String?,
        @PathVariable("user") user: String,
        @PathVariable("fileType") fileType: String,
        @PathVariable("fileName") fileName: String
    ): Mono<ResponseEntity<ByteArray>>? {
        return try {
            Mono.just(videoStreamService.prepareContent(user, fileName, fileType, httpRangeList))
        } catch (e: IOException) {
            Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())
        }
    }
}