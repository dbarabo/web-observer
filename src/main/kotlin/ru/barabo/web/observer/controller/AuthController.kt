package ru.barabo.web.observer.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import ru.barabo.web.observer.config.AuthEntryPointJwt
import ru.barabo.web.observer.config.JwtUtils
import ru.barabo.web.observer.config.LdapUserDetails
import ru.barabo.web.observer.controller.request.LoginRequest
import ru.barabo.web.observer.controller.response.JwtResponse
import java.util.stream.Collectors

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/auth")
class AuthController {

    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @Autowired
    var authenticationManager: AuthenticationManager? = null

    @Autowired
    var jwtUtils: JwtUtils? = null

    @PostMapping("/login")
    fun authenticateUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {

        logger.error("loginRequest.username=${loginRequest.username}")
        logger.error("loginRequest.password=${loginRequest.password}")

        val authentication = authenticationManager!!.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils!!.generateJwtToken(authentication)

        val userDetails: LdapUserDetails = authentication.principal as LdapUserDetails
        val roles: List<String> = userDetails.authorities.stream()
            .map { item -> item.authority }
            .collect(Collectors.toList())

        return ResponseEntity.ok<Any>(
            JwtResponse(
                jwt,
                userDetails.user,
                roles
            )
        )
    }
}