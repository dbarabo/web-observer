package ru.barabo.web.observer.entity

//@Entity
class Elem() {

    constructor(id: Long, name: String) : this() {
        this.id = id
        this.name = name
    }

    //@Id
    var id: Long? = null

    var name: String = ""

    override fun equals(other: Any?): Boolean {
        other ?: return false

        if (this === other) return true

        //if (javaClass != ProxyUtils.getUserClass(other)) return false

        other as Elem

        return this.id != null && this.id == other.id
    }

    override fun hashCode() = 9973

    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id)"
    }
}