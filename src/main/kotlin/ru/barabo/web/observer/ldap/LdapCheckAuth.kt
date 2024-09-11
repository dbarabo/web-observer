package ru.barabo.web.observer.ldap

import org.apache.directory.api.ldap.model.entry.Entry
import org.apache.directory.api.ldap.model.message.*
import org.apache.directory.api.ldap.model.name.Dn
import org.apache.directory.ldap.client.api.LdapConnection
import org.apache.directory.ldap.client.api.LdapConnectionConfig
import org.apache.directory.ldap.client.api.LdapNetworkConnection
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class LdapCheckAuth() {

    fun check(username: String, password: String): Boolean {

        val connection: LdapConnection = getConnectionAndAuth(username, password)

        var fullName: String

        return try {
            fullName = checkRoleOneWin(connection, username)

            true
        } catch (e: Exception) {
            throw UsernameNotFoundException("Ошибка проверки прав: ${e.message}")

        } finally {
            try {
                connection.close()
            } catch (e: Exception) {}
        }
    }

    private fun checkRoleOneWin(connection: LdapConnection, username: String): String {

        val req: SearchRequest = SearchRequestImpl()
        req.setScope( SearchScope.SUBTREE)
        req.addAttributes( "memberOf" )
        req.setTimeLimit( 0 )
        req.setBase( Dn( "OU=Bank,DC=ptkb,DC=local" ) )
        req.setFilter( "(sAMAccountName=$username)")

        val searchCursor = connection.search(req)

        var fullName: String = ""

        var isOneWindow = false

        while (searchCursor.next()) {
            val response: Response = searchCursor.get()

            if (response is SearchResultEntry) {
                val resultEntry: Entry = response.entry

                fullName = response.entry.dn.rdn.toString() //dn.rdn=CN=Кистюк Ольга Николаевна

                val attr = resultEntry.get("memberof") ?: continue

                isOneWindow = attr.contains("CN=onewindow,OU=Special Gruppen,OU=Bank,DC=ptkb,DC=local")
            }
        }
        if(!isOneWindow) {
            throw UsernameNotFoundException("Пользователь $fullName не имеет доступа к 'одному окну'. Обратитесь к администратору")
        }

        return fullName
    }

    private fun getConnectionAndAuth(username: String, password: String): LdapConnection {
        lateinit var connection: LdapConnection
        try {
            val config = LdapConnectionConfig()
            config.ldapHost = "192.168.0.9"
            config.ldapPort = 389
            config.name =  "${username.trim()}@ptkb.local"
            config.credentials = password

            connection = LdapNetworkConnection(config)
            connection.bind()
        } catch (e: Exception) {
            try {
                connection.close()
            } catch (e1: Exception) {}

            throw UsernameNotFoundException("Ошибка аунтефикации: ${e.message}")
        }

        if((!connection.isConnected) || (!connection.isAuthenticated)) {
            try {
                connection.close()
            } catch (e1: Exception) {}

            throw UsernameNotFoundException("Неверный пароль")
        }

        return connection
    }
}