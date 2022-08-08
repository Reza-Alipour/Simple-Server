package com.example.simpleWebServer.utils

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
        const val JWT_TOKEN_VALIDITY = (60 * 60 * 24 * 1000).toLong()
    }


    private fun isTokenTimeValid(tokenClaims: Claims): Boolean {
        return tokenClaims.expiration.after(Date())
    }

    fun generateToken(userId: Long): String {
        return Jwts.builder().setIssuer(userId.toString()).setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
            .signWith(SignatureAlgorithm.HS512, secret).compact()
    }

    fun getUserId(token: String): Long? {
        return getTokenBody(token).let {
            if (isTokenTimeValid(it)) it.issuer.toLong() else null
        }
    }

    fun getUser(token: String): User? {
        return getUserId(token)?.let { userRepository.findById(it).orElse(null) }
    }


    private fun getTokenBody(token: String): Claims {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
    }
}