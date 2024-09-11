package ru.barabo.web.observer.main

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["ru.barabo.web.observer.config", "ru.barabo.web.observer.controller", "ru.barabo.web.observer.ldap"])
class WebObserver {
}

fun main(args: Array<String>) {
    runApplication<WebObserver>(*args)
}