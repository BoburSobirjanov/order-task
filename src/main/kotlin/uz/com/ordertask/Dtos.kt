package uz.com.ordertask

import jakarta.validation.constraints.Size
import java.math.BigDecimal


data class BaseMessage(val code: Int, val message: String?)
data class UserCreateRequest(
        var fullName: String,
        @field:Size(min = 5, max = 20) var username: String,
        var address: String,
        var email: String,
        var userRole: String
) {
    fun toEntity(role: UserRole): UserEntity {
        return UserEntity(fullName, username, email, address, role)
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

data class UserCountOfProductResponse(
        var productId: Long,
        var countOfUser: Int
){
    companion object{
        fun toResponse(productEntity: ProductEntity, countOfUsers: Int):UserCountOfProductResponse{
            productEntity.run {
                return UserCountOfProductResponse(productEntity.id!!,countOfUsers)
            }
        }
    }
}

data class ProductUpdateRequest(
        var price: BigDecimal?,
        var stockCount: Int?
)

data class OrderCreateRequest(
        var userId: Long,
        var orderItems: List<OrderItemCreateRequestList>,
        var orderStatus: OrderStatus = OrderStatus.PENDING,
        var paymentEntity: PaymentCreateRequest
) {
    fun toEntity(user: UserEntity, totalAmount: BigDecimal): OrderEntity {
        return OrderEntity(user, orderStatus, totalAmount)
    }

}

data class OrderItemCreateRequestList(
        var productId: Long,
        var quantity: Int
)


data class PaymentCreateRequest(
        var userId: Long,
        var orderId: Long,
        var paymentMethod: PaymentMethod
) {
    fun toEntity(user: UserEntity, orderEntity: OrderEntity): PaymentEntity {
        return PaymentEntity(orderEntity, user, paymentMethod, orderEntity.totalAmount)
    }
}

data class PaymentResponse(
        var id: Long,
        var orderId: OrderEntity,
        var paymentMethod: PaymentMethod,
        var amount: BigDecimal
) {
    companion object {
        fun toResponse(paymentEntity: PaymentEntity): PaymentResponse {
            paymentEntity.run {
                return PaymentResponse(id!!, order, paymentMethod, amount)
            }
        }
    }
}

data class OrderItemsCreateRequest(
        var orderId: Long,
        var productId: Long,
        var quantity: Int
) {
    fun toEntity(orderEntity: OrderEntity, productEntity: ProductEntity): OrderItemEntity {
        val totalPrice = productEntity.price.multiply(BigDecimal(quantity))
        return OrderItemEntity(orderEntity, productEntity, quantity, productEntity.price, totalPrice)
    }
}

data class OrderItemsUpdateRequest(
        var productId: Long?,
        var quantity: Int?
)

data class OrderResponse(
        var id: Long,
        var username: String,
        var orderItems: List<OrderItemsResponse>?
) {
    companion object {
        fun toResponse(orderEntity: OrderEntity,orderItems: List<OrderItemsResponse>?): OrderResponse {
            orderEntity.run {
                return OrderResponse(id!!, user.username, orderItems)
            }
        }
    }
}
data class OrderItemsResponse(
        var id: Long,
        var productId: ProductEntity,
        var unitPrice: BigDecimal,
        var totalPrice: BigDecimal
) {
    companion object {
        fun toResponse(orderItemEntity: OrderItemEntity): OrderItemsResponse {
            orderItemEntity.run {
                return OrderItemsResponse(id!!, product, unitPrice, totalPrice)
            }
        }
    }
}