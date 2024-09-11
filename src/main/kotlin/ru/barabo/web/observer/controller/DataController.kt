package ru.barabo.web.observer.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.barabo.web.observer.entity.Elem

@CrossOrigin(origins = ["http://localhost:8082"], /*maxAge = 3600,*/ allowCredentials = "true")
@RestController
@RequestMapping("/api")
class DataController(private val elemRepository: ElemRepository) {

    @GetMapping("/clients")
    fun getData(): Iterable<Elem> {
        return elemRepository.findElems()
    }
}