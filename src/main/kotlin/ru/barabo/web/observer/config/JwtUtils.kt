package ru.barabo.web.observer.config

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import ru.barabo.web.observer.controller.AuthController
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtUtils {

    @Value("\${barabo.jwt.jwtSecret}")
    private var jwtSecret: String? = null

    @Value("\${barabo.jwt.jwtExpirationMs}")
    private var jwtExpirationMs = 0

    private val logger = LoggerFactory.getLogger(JwtUtils::class.java)

    fun generateJwtToken(authentication: Authentication): String {
        val userPrincipal: LdapUserDetails = authentication.principal as LdapUserDetails

        return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact()
    }

    private fun key(): SecretKey {
        logger.error("jwtSecret=$jwtSecret")

        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
    }

    fun getUserNameFromJwtToken(token: String?): String {

        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).body.subject
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken)

            return true
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }

        return false
    }
}