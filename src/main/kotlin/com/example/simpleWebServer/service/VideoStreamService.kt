package com.example.simpleWebServer.service

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class VideoStreamService {

    companion object {
        private const val CONTENT_TYPE = "Content-Type"
        private const val CONTENT_LENGTH = "Content-Length"
        private const val VIDEO_CONTENT = "video/"
        private const val BYTES = "bytes"
        private const val CHUNK_SIZE: Long = 3147000
        private const val LOCATION = "upload-dir"

    }

    @Throws(IOException::class)
    fun prepareContent(
        username: String,
        fileName: String,
        fileType: String,
        range: String?
    ): ResponseEntity<ByteArray> {
        val filePath = Paths.get("$LOCATION/$username/${fileName}.$fileType")
        var rangeStart: Long = 0
        var rangeEnd: Long = CHUNK_SIZE
        val fileSize = Files.size(filePath)

        if (range == null) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).header(CONTENT_TYPE, VIDEO_CONTENT + fileType)
                .header(HttpHeaders.ACCEPT_RANGES, BYTES).header(CONTENT_LENGTH, rangeEnd.toString())
                .header(HttpHeaders.CONTENT_RANGE, "$BYTES $rangeStart-$rangeEnd/$fileSize")
                .header(CONTENT_LENGTH, fileSize.toString())
                .body(readByteRangeNew(filePath, rangeStart, rangeEnd)) // Read the object and convert it as bytes
        }

        val ranges = range.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        rangeStart = ranges[0].substring(6).toLong()
        rangeEnd = if (ranges.size > 1) ranges[1].toLong() else rangeStart + CHUNK_SIZE
        rangeEnd = rangeEnd.coerceAtMost(fileSize - 1)

        val data = readByteRangeNew(filePath, rangeStart, rangeEnd)
        val contentLength = (rangeEnd - rangeStart + 1).toString()


        return ResponseEntity.status(if (rangeEnd >= fileSize) HttpStatus.OK else HttpStatus.PARTIAL_CONTENT)
            .header(CONTENT_TYPE, VIDEO_CONTENT + fileType)
            .header(HttpHeaders.ACCEPT_RANGES, BYTES).header(CONTENT_LENGTH, contentLength)
            .header(HttpHeaders.CONTENT_RANGE, "$BYTES $rangeStart-$rangeEnd/$fileSize")
            .body(data)

    }

    @Throws(IOException::class)
    fun readByteRangeNew(filePath: Path, start: Long, end: Long): ByteArray {
        val data = Files.readAllBytes(filePath)
        val result = ByteArray((end - start).toInt() + 1)
        System.arraycopy(data, start.toInt(), result, 0, (end - start).toInt() + 1)
        return result
    }
}