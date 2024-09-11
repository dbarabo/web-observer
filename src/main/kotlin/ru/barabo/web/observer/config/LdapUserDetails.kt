package ru.barabo.web.observer.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class LdapUserDetails(var user: String = "", var pswd: String = "",
                      var token: String = "") : UserDetails {

        private val authority = listOf(GrantedAuthority { "READ_POST_PUT_GET" })

        override fun getAuthorities(): List<GrantedAuthority> {
            return  authority
        }

        override fun getPassword(): String = pswd

        override fun getUsername(): String = user

        override fun isAccountNonExpired(): Boolean = true

        override fun isAccountNonLocked(): Boolean = true

        override fun isCredentialsNonExpired(): Boolean = true

        override fun isEnabled(): Boolean = true

        override fun equals(other: Any?): Boolean {
            return if (other is LdapUserDetails) this.user == other.user else false
        }

        override fun hashCode(): Int {
            return this.username.hashCode()
        }
}
