package com.example.simpleWebServer.controller

import com.example.simpleWebServer.dto.LoginDTO
import com.example.simpleWebServer.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("login")
class LoginController(private val userService: UserService) {
    companion object {
        const val COOKIE_MAX_AGE = 60 * 60 * 24
    }

    @PostMapping
    fun login(@RequestBody loginDTO: LoginDTO, response: HttpServletResponse): ResponseEntity<String> {
        val jwtAndRole = userService.login(loginDTO.username, loginDTO.password) ?: return ResponseEntity.badRequest()
            .body("Invalid username or password")
        val cookie = Cookie("jwt", jwtAndRole.first)
        cookie.isHttpOnly = true
        cookie.maxAge = COOKIE_MAX_AGE
        response.addCookie(cookie)
        return ResponseEntity.ok(jwtAndRole.second)
    }

    @GetMapping("logout")
    fun logout(@CookieValue("jwt") jwt: String, response: HttpServletResponse): ResponseEntity<String> {
        val cookie = Cookie("jwt", "")
        cookie.maxAge = 0
        response.addCookie(cookie)
        return ResponseEntity.ok("Logout successful")
    }
}