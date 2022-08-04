package com.example.simpleWebServer.utils

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*


@Configuration
@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Throws(java.lang.Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = Arrays.asList("*")
        configuration.allowedMethods = Arrays.asList("*")
        configuration.allowedHeaders = Arrays.asList("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
