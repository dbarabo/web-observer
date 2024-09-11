package ru.barabo.web.observer.controller

import org.springframework.stereotype.Service
import ru.barabo.web.observer.entity.Elem

@Service
class ElemRepository {

    fun findElems(): Iterable<Elem> {
        return listOf(
            Elem(0, "test"),
            Elem(1, "fullname")
            )
    }
}