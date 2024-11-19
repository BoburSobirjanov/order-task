package uz.com.ordertask

import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class UserCreateRequest(
        var fullName: String,
        @field:Size(min = 5, max = 20) var username: String,
        var address: String,
        var email: String,
        var userRole: UserRole
) {
    fun toEntity(): UserEntity {
        return UserEntity(fullName, username, email, address, userRole)
    }
}

data class UserResponse(
        var id: Long,
        var fullName: String,
        var username: String,
        var email: String,
        var address: String,
        var userRole: UserRole
) {
    companion object {
        fun toResponse(userEntity: UserEntity): UserResponse {
            userEntity.run {
                return UserResponse(id!!, fullName, username, email, address, userRole)
            }
        }
    }
}

data class UserUpdateRequest(
        var fullName: String?,
        var username: String?,
        var email: String?,
        var address: String?
)

data class CategoryCreateRequest(
        var name: String,
        var description: String
) {
    fun toEntity(): CategoryEntity {
        return CategoryEntity(name, description)
    }
}

data class CategoryResponse(
        var id: Long,
        var name: String,
        var description: String
) {
    companion object {
        fun toResponse(categoryEntity: CategoryEntity): CategoryResponse {
            categoryEntity.run {
                return CategoryResponse(id!!, name, description)
            }
        }
    }
}

data class CategoryUpdateRequest(
        var name: String?,
        var description: String?
)

data class ProductCreateRequest(
        var name: String,
        var description: String,
        var price: BigDecimal,
        var stockCount: Int,
        var categoryId: Long
) {
    fun toEntity(categoryEntity: CategoryEntity): ProductEntity {
        return ProductEntity(name, description, price, stockCount, categoryEntity)
    }
}

data class ProductResponse(
        var id: Long,
        var name: String,
        var description: String,
        var price: BigDecimal,
        var stockCount: Int,
        var categoryName: String
) {
    companion object {
        fun toResponse(productEntity: ProductEntity): ProductResponse {
            productEntity.run {
                return ProductResponse(id!!, name, description, price, stockCount, category.name)
            }
        }
    }
}

data class ProductUpdateRequest(
        var price: BigDecimal?,
        var stockCount: Int?
)