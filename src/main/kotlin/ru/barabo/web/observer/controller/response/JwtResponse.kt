package ru.barabo.web.observer.controller.response

class JwtResponse(var token: String="", var username: String="", var roles: List<String> = emptyList())