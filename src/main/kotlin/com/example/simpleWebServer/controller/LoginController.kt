package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.LoginDTO
import com.example.simpleWebServer.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/login")
class LoginController(private val userService: UserService) {
    companion object {
        const val COOKIE_MAX_AGE = 60 * 60 * 24
    }

    @PostMapping
    fun login(@RequestBody loginDTO: LoginDTO, response: HttpServletResponse): ResponseEntity<String> {
        val jwt: String = userService.login(loginDTO.username, loginDTO.password) ?: return ResponseEntity.badRequest()
            .body("Invalid username or password")
        val cookie = Cookie("jwt", jwt)
        cookie.isHttpOnly = true
        cookie.maxAge = COOKIE_MAX_AGE
        response.addCookie(cookie)
        return ResponseEntity.ok("Login successful")
    }
}