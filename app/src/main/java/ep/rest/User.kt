package ep.rest

import java.io.Serializable

data class User(
        val id: Int = 0,
        val ime: String = "",
        val priimek: String = "",
        val naslov: String = "",
        val email: String = "",
        val isActive: Int = 0,
        val is_verified: Int = 0) : Serializable
