package ru.barabo.web.observer.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.barabo.web.observer.ldap.LdapAuthenticationProvider
import ru.barabo.web.observer.ldap.LdapCheckAuth
import java.util.*


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(var ldapCheckAuth: LdapCheckAuth, var ldapUserDetailsService: LdapUserDetailsService) {

    private lateinit var provider: AuthenticationProvider

    @Autowired
    private val unauthorizedHandler: AuthEntryPointJwt? = null

    @Bean
    fun authenticationProvider(): AuthenticationProvider {

        if(::provider.isInitialized) return provider

        provider = LdapAuthenticationProvider(ldapCheckAuth, ldapUserDetailsService)

        return provider
    }

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter {
        return AuthTokenFilter()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {

        val configuration = CorsConfiguration()
        //configuration.allowedOrigins = Collections.singletonList("*")  // listOf("*")
        configuration.setAllowedOriginPatterns( Collections.singletonList("*") )
        configuration.allowedMethods = Collections.singletonList("*") //listOf("*")
        configuration.allowedHeaders = Collections.singletonList("*") //listOf("*")

        configuration.allowCredentials = false //updated to false
        //configuration.addAllowedOrigin("*")
        configuration.addAllowedHeader("*")
        configuration.addAllowedMethod("GET")
        configuration.addAllowedMethod("PUT")
        configuration.addAllowedMethod("POST")

        //.allowedHeaders("x-auth-token","Authorization","Access-Control-Allow-Origin","Access-Control-Allow-Credentials")


        configuration.allowCredentials = true

        configuration.exposedHeaders = listOf("Authorization", "x-access-token", "x-tok")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.authenticationProvider(authenticationProvider() )
        return authenticationManagerBuilder.build()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity, authenticationManager: AuthenticationManager): SecurityFilterChain {

        http.cors()

        http.csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }

            //.logout { it.disable() }

            .exceptionHandling { exception: ExceptionHandlingConfigurer<HttpSecurity?> ->
                exception.authenticationEntryPoint(
                    unauthorizedHandler
                )
            }
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }

            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/auth/login").permitAll() //"/auth/**"
                    .requestMatchers("/*", "/login", "/js/*", "/css/*").permitAll()
                    .anyRequest().authenticated()
            }

        http.authenticationProvider(authenticationProvider() )

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}