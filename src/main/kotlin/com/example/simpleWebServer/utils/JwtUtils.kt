package com.example.simpleWebServer.utils

import com.example.simpleWebServer.dto.UserDTO
import com.example.simpleWebServer.entity.User
import com.example.simpleWebServer.repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*


@Component
class JwtUtils : Serializable {
    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {
        private const val serialVersionUID = -2550185165626007488L
        const val JWT_TOKEN_VALIDITY = (60 * 24 * 1000).toLong()
    }


    private fun isTokenExpired(token: String): Boolean {
        val expiration: Date = getTokenBody(token).expiration
        return expiration.before(Date())
    }

    fun generateToken(userId: Long): String {
        return Jwts.builder()
            .setIssuer(userId.toString())
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS512, secret).compact()
    }

    fun generateToken(user: UserDTO): String {
        return Jwts.builder()
            .setIssuer(user.id.toString())
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS512, secret).compact()
    }

    fun validateToken(token: String, user: UserDTO): Boolean {
        return getTokenBody(token).issuer == user.id.toString()
    }

    fun validateToken(token: String, userId: Long): Boolean {
        return getTokenBody(token).issuer == userId.toString()
    }

    fun getUserId(token: String): Long {
        return getTokenBody(token).issuer.toLong()
    }

    fun getUser(token: String): User? {
        return userRepository.findById(getUserId(token)).orElse(null)
    }


    private fun getTokenBody(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }
}