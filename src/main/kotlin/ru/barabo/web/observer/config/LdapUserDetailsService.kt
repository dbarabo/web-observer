package ru.barabo.web.observer.config

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class LdapUserDetailsService : UserDetailsService {

    val userStore: MutableList<LdapUserDetails> = ArrayList()

    override fun loadUserByUsername(username: String?): UserDetails =
        username?.let { findByUserName(it) } ?: throw UsernameNotFoundException("User is null")

    fun findByUserName(userName: String): UserDetails? = userStore.firstOrNull { it.username == userName }

    fun getOrAddUser(userName: String, pswd: String): UserDetails = findByUserName(userName) ?: addUser(userName, pswd)

    private fun addUser(userName: String, pswd: String): UserDetails =
        LdapUserDetails(userName, pswd).apply { userStore += this }
}