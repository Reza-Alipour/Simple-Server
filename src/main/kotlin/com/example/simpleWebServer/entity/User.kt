package com.example.simpleWebServer.entity

import com.example.simpleWebServer.dto.UserDTO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*


@Entity
@Table(name = "user_table")
class User(
    var role: RoleType,
    var strike: Boolean = false,
    var videosCount: Int = 0,
    @Id @GeneratedValue var id: Long? = null,
) : ToDTO {

    constructor() : this(RoleType.USER)

    @Column(unique = true)
    var username: String = ""

    var passwordHash: String? = null
        set(value) {
            field = BCryptPasswordEncoder().encode(value)
        }


    fun comparePasswrd(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.passwordHash)
    }

    override fun toDTO(): UserDTO {
        return UserDTO(
            id = this.id ?: -1,
            username = this.username,
            strike = this.strike,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + username.hashCode()
        return result
    }
}

enum class RoleType {
    ADMIN_PENDING, ADMIN, MANAGER, USER
}
