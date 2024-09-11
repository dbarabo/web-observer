package ru.barabo.web.observer.ldap

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import ru.barabo.web.observer.config.LdapUserDetailsService
import ru.barabo.web.observer.controller.AuthController

@Component
class LdapAuthenticationProvider(var ldapCheckAuth: LdapCheckAuth, var ldapUserDetailsService: LdapUserDetailsService) : AuthenticationProvider {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    override fun authenticate(authentication: Authentication?): Authentication {

        logger.error("LdapAuthenticationProvider.authentication=$authentication")

        val userToken = authentication as UsernamePasswordAuthenticationToken

        val username = userToken.name
        val password = authentication.getCredentials() as String

        logger.error("userToken.name=$username")
        logger.error("getCredentials=$password")

        if(ldapCheckAuth.check(username, password)) {

            logger.error("ldapCheckAuth.check!")

            val userDetail = ldapUserDetailsService.getOrAddUser(username, password)

            return UsernamePasswordAuthenticationToken.authenticated(
                userDetail,
                password,
                userDetail.authorities
            )

        } else {
            logger.error("NO ldapCheckAuth.check")
            throw BadCredentialsException("user $username is not authenticated")
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}