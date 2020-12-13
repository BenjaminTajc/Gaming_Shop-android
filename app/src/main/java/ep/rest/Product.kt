package ep.rest

import java.io.Serializable

data class Product(
        val id: Int = 0,
        val ime: String = "",
        val opis: String = "",
        val cena: Double = 0.0,
        val isActive: Int = 0,
        val creatorId: Int = 0) : Serializable
