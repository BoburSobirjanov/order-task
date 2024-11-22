package uz.com.ordertask

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
interface UserService {
    fun create(request: UserCreateRequest)
    fun getOne(id: Long): UserResponse
    fun update(id: Long, request: UserUpdateRequest)
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<UserResponse>
}

@Service
class UserServiceImpl(
        private val userRepository: UserRepository
) : UserService {
    override fun create(request: UserCreateRequest) {
        request.run {
            val validRole = try {
                UserRole.valueOf(userRole.uppercase(Locale.getDefault()))
            } catch (e: IllegalArgumentException) {
                throw UserBadRequestException()
            }
            val user = userRepository.findByUsernameAndDeletedFalse(username)
            if (user != null) throw UserHasAlreadyExistsException()
            userRepository.save(this.toEntity(validRole))
        }
    }

    override fun getOne(id: Long): UserResponse {
        return userRepository.findByIdAndDeletedFalse(id)?.let {
            UserResponse.toResponse(it)
        } ?: throw UserNotFoundException()
    }

    override fun update(id: Long, request: UserUpdateRequest) {
        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()
        request.run {
            username?.let {
                val usernameAndDeletedFalse = userRepository.findByUsername(id, it)
                if (usernameAndDeletedFalse != null) throw UserHasAlreadyExistsException()
                user.username = it
            }
            fullName?.let { request.fullName = it }
            email?.let { request.email = it }
            address?.let { request.address = it }
        }
        userRepository.save(user)
    }

    @Transactional
    override fun delete(id: Long) {
        userRepository.trash(id)
    }

    override fun getAll(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAllNotDeletedForPageable(pageable).map {
            UserResponse.toResponse(it)
        }
    }

}

@Service
interface CategoryService {
    fun create(request: CategoryCreateRequest)
    fun getOne(id: Long): CategoryResponse
    fun update(id: Long, request: CategoryUpdateRequest)
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<CategoryResponse>
}

@Service
class CategoryServiceImpl(
        private val categoryRepository: CategoryRepository
) : CategoryService {
    override fun create(request: CategoryCreateRequest) {
        request.run {
            val categoryEntity = categoryRepository.findByNameAndDeletedFalse(name)
            if (categoryEntity != null) throw CategoryHasAlreadyExistsException()
            categoryRepository.save(this.toEntity())
        }
    }

    override fun getOne(id: Long): CategoryResponse {
        return categoryRepository.findByIdAndDeletedFalse(id)?.let {
            CategoryResponse.toResponse(it)
        } ?: throw CategoryNotFoundException()
    }

    override fun update(id: Long, request: CategoryUpdateRequest) {
        val categoryEntity = categoryRepository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundException()
        request.run {
            name?.let {
                val category = categoryRepository.findByName(id, it)
                if (category != null) throw CategoryHasAlreadyExistsException()
                categoryEntity.name = it
            }
            description?.let { request.description = it }
        }
        categoryRepository.save(categoryEntity)
    }

    @Transactional
    override fun delete(id: Long) {
        categoryRepository.trash(id)
    }

    override fun getAll(pageable: Pageable): Page<CategoryResponse> {
        return categoryRepository.findAllNotDeletedForPageable(pageable).map {
            CategoryResponse.toResponse(it)
        }
    }

}

interface ProductService {
    fun create(request: ProductCreateRequest)
    fun getOne(id: Long): ProductResponse
    fun update(id: Long, request: ProductUpdateRequest)
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<ProductResponse>
    fun getUserCountForProduct(productId: Long, pageable: Pageable): Page<UserCountOfProductResponse>
}

@Service
class ProductServiceImpl(
        private val productRepository: ProductRepository,
        private val categoryRepository: CategoryRepository,
        private val orderItemRepository: OrderItemRepository,
        private val entityManager: EntityManager
) : ProductService {
    override fun create(request: ProductCreateRequest) {
        request.run {
            categoryRepository.findByIdAndDeletedFalse(categoryId) ?: throw CategoryNotFoundException()
            val categoryEntity = entityManager.getReference(CategoryEntity::class.java, categoryId)
            productRepository.save(this.toEntity(categoryEntity))
        }
    }

    override fun getOne(id: Long): ProductResponse {
        return productRepository.findByIdAndDeletedFalse(id)?.let {
            ProductResponse.toResponse(it)
        } ?: throw ProductNotFoundException()
    }

    override fun update(id: Long, request: ProductUpdateRequest) {
        val productEntity = productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException()
        request.run {
            price?.let { request.price = it }
            stockCount?.let { request.stockCount = it }
        }
        productRepository.save(productEntity)
    }

    @Transactional
    override fun delete(id: Long) {
        productRepository.trash(id)
    }

    override fun getAll(pageable: Pageable): Page<ProductResponse> {
        return productRepository.findAllNotDeletedForPageable(pageable).map {
            ProductResponse.toResponse(it)
        }
    }

    override fun getUserCountForProduct(productId: Long, pageable: Pageable): Page<UserCountOfProductResponse> {
        val productEntity = productRepository.findById(productId).orElseThrow {
            throw ProductNotFoundException()
        }
        val orderItems = orderItemRepository.findAllByProductIdAndDeletedFalse(productId)
        val userSet = orderItems.mapNotNull { it.order.user.id }.toSet()
        val countOfBoughtUser = userSet.size
        val response = UserCountOfProductResponse.toResponse(productEntity, countOfBoughtUser)
        val responses = listOf(response)
        return PageImpl(responses, pageable, 1)
    }

}


interface OrderService {
    fun create(request: OrderCreateRequest)
    fun getOne(id: Long): OrderResponse
    fun delete(id: Long)
    fun cancel(id: Long, userId: Long)
    fun changeOrderStatus(id: Long, userId: Long, status: String)
    fun getAll(userId: Long?,
               startTime: String?,
               endTime: String?,
               pageable: Pageable): Page<OrderResponse>

    fun getUserOrders(userId: Long, pageable: Pageable): Page<OrderResponse>
}

@Service
class OrderServiceImpl(
        private val orderRepository: OrderRepository,
        private val userRepository: UserRepository,
        private val entityManager: EntityManager,
        private val orderItemService: OrderItemService,
        private val paymentService: PaymentService,
        private val orderItemRepository: OrderItemRepository
) : OrderService {
    override fun create(request: OrderCreateRequest) {
        request.run {
            userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException()
            val user = entityManager.getReference(UserEntity::class.java, userId)
            val orderEntity = orderRepository.save(this.toEntity(user, BigDecimal.ZERO))
            for (item in request.orderItems) {
                val itemsWithOrderId = OrderItemsCreateRequest(
                        productId = item.productId,
                        quantity = item.quantity,
                        orderId = orderEntity.id!!
                )
                orderItemService.create(itemsWithOrderId)
            }
            request.paymentEntity.run {
                if (paymentEntity.paymentMethod != PaymentMethod.HUMO || paymentEntity.paymentMethod != PaymentMethod.UZCARD
                        || paymentEntity.paymentMethod != PaymentMethod.PAYME
                        || paymentEntity.paymentMethod != PaymentMethod.CASH) throw UserBadRequestException()
                val paymentCreateRequest = PaymentCreateRequest(
                        userId = user.id!!,
                        orderId = orderEntity.id!!,
                        paymentMethod = paymentEntity.paymentMethod
                )
                paymentService.create(paymentCreateRequest)
            }
        }
    }

    override fun getOne(id: Long): OrderResponse {
        val orderEntity = orderRepository.findByIdAndDeletedFalse(id) ?: throw OrderNotFoundException()
        val orderItems = orderEntity.id?.let { orderItemRepository.findByOrderIdAndDeletedFalse(it) }
        val orderItemsResponse = orderItems?.map { OrderItemsResponse(it.id!!, it.product, it.unitPrice, it.totalPrice) }
        return OrderResponse.toResponse(orderEntity, orderItemsResponse)
    }

    @Transactional
    override fun delete(id: Long) {
        orderRepository.trash(id) ?: throw OrderNotFoundException()
    }

    override fun cancel(id: Long, userId: Long) {
        val orderEntity = orderRepository.findByIdAndDeletedFalse(id) ?: throw OrderNotFoundException()
        val userEntity = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException()
        if (orderEntity.user.id != userEntity.id && orderEntity.orderStatus != OrderStatus.PENDING) throw UserBadRequestException()
        orderEntity.orderStatus = OrderStatus.CANCELLED
        orderRepository.save(orderEntity)
    }

    override fun changeOrderStatus(id: Long, userId: Long, status: String) {
        val orderEntity = orderRepository.findByIdAndDeletedFalse(id) ?: throw OrderNotFoundException()
        val userEntity = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException()
        if (userEntity.userRole != UserRole.ADMIN && orderEntity.orderStatus == OrderStatus.CANCELLED) throw UserBadRequestException()
        val orderStatus = try {
            OrderStatus.valueOf(status.uppercase(Locale.getDefault()))
        } catch (e: IllegalArgumentException) {
            throw UserBadRequestException()
        }
        orderEntity.orderStatus = orderStatus
        orderRepository.save(orderEntity)

    }

    override fun getAll(userId: Long?,
                        startTime: String?,
                        endTime: String?,
                        pageable: Pageable): Page<OrderResponse> {
        val startDateTime = startTime?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
        val endDateTime = endTime?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }

        val spec = Specification.where(OrderSpecification.hasUser(userId))
                .and(OrderSpecification.isCreatedBetween(startDateTime, endDateTime))
                .and(OrderSpecification.isNotDeleted())

        val orders = orderRepository.findAll(spec, pageable)
        return orders.map { order ->
            val orderItems = order.id?.let { orderItemRepository.findByOrderIdAndDeletedFalse(it) }
            val orderItemsResponse = orderItems?.map {
                OrderItemsResponse(it.id!!, it.product, it.unitPrice, it.totalPrice)
            }

            OrderResponse.toResponse(order, orderItemsResponse ?: emptyList())
        }
    }

    override fun getUserOrders(userId: Long, pageable: Pageable): Page<OrderResponse> {
        val userEntity = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException()
        val orders = orderRepository.findAllByUserAndDeletedFalse(userEntity, pageable)
        return orders.map { order ->
            val orderItems = order.id?.let { orderItemRepository.findByOrderIdAndDeletedFalse(it) }
            val orderItemsResponse = orderItems?.map { OrderItemsResponse(it.id!!, it.product, it.unitPrice, it.totalPrice) }
            OrderResponse.toResponse(order, orderItemsResponse)

        }
    }
}


interface OrderItemService {
    fun create(request: OrderItemsCreateRequest)
    fun getOne(id: Long): OrderItemsResponse
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<OrderItemsResponse>
    fun update(id: Long, request: OrderItemsUpdateRequest)
}

@Service
class OrderItemServiceImpl(
        private val orderItemRepository: OrderItemRepository,
        private val orderRepository: OrderRepository,
        private val productRepository: ProductRepository,
        private val entityManager: EntityManager,
        private val userRepository: UserRepository
) : OrderItemService {
    override fun create(request: OrderItemsCreateRequest) {
        request.run {
            orderRepository.findByIdAndDeletedFalse(orderId) ?: throw OrderNotFoundException()
            productRepository.findByIdAndDeletedFalse(productId) ?: throw ProductNotFoundException()
            val orderEntity = entityManager.getReference(OrderEntity::class.java, orderId)
            val productEntity = entityManager.getReference(ProductEntity::class.java, productId)
            orderEntity.user.id?.let {
                userRepository.findByIdAndDeletedFalse(it)
                        ?: throw UserNotFoundException()
            }
            if (productEntity.stockCount < request.quantity) throw ProductHasNoEnough()
            orderEntity.totalAmount = productEntity.price.multiply(BigDecimal(request.quantity))
            productEntity.stockCount -= request.quantity
            productRepository.save(productEntity)
            orderItemRepository.save(this.toEntity(orderEntity, productEntity))
        }
    }

    override fun getOne(id: Long): OrderItemsResponse {
        return orderItemRepository.findByIdAndDeletedFalse(id)?.let {
            OrderItemsResponse.toResponse(it)
        } ?: throw OrderItemNotFoundException()
    }

    @Transactional
    override fun delete(id: Long) {
        orderItemRepository.trash(id) ?: throw OrderItemNotFoundException()
    }

    override fun getAll(pageable: Pageable): Page<OrderItemsResponse> {
        return orderItemRepository.findAllNotDeletedForPageable(pageable).map {
            OrderItemsResponse.toResponse(it)
        }
    }

    override fun update(id: Long, request: OrderItemsUpdateRequest) {
        val orderItem = orderItemRepository.findByIdAndDeletedFalse(id) ?: throw OrderItemNotFoundException()
        val productEntity = productRepository.findByIdAndDeletedFalse(request.productId!!)?:throw ProductNotFoundException()
        orderItem.run {
            product = productEntity
            quantity=request.quantity!!
            totalPrice=product.price.multiply(request.quantity?.let { BigDecimal(it) } ?: BigDecimal.ZERO)
            unitPrice=productEntity.price
        }
        orderItemRepository.save(orderItem)
    }


}

interface PaymentService {
    fun create(request: PaymentCreateRequest)
    fun getUserPayments(userId: Long, pageable: Pageable): Page<PaymentResponse>
    fun getOne(id: Long): PaymentResponse
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<PaymentResponse>
}

@Service
class PaymentServiceImpl(
        private val paymentRepository: PaymentRepository,
        private val userRepository: UserRepository,
        private val orderRepository: OrderRepository,
        private val entityManager: EntityManager
) : PaymentService {
    override fun create(request: PaymentCreateRequest) {
        request.run {
            userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException()
            orderRepository.findByIdAndDeletedFalse(orderId) ?: throw OrderNotFoundException()
            val user = entityManager.getReference(UserEntity::class.java, userId)
            val order = entityManager.getReference(OrderEntity::class.java, orderId)
            paymentRepository.save(this.toEntity(user, order))
        }
    }

    override fun getUserPayments(userId: Long, pageable: Pageable): Page<PaymentResponse> {
        val userEntity = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundException()
        return paymentRepository.findAllByUserAndDeletedFalse(userEntity, pageable).map {
            PaymentResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): PaymentResponse {
        return paymentRepository.findByIdAndDeletedFalse(id)?.let {
            PaymentResponse.toResponse(it)
        } ?: throw PaymentNotFoundException()
    }

    @Transactional
    override fun delete(id: Long) {
        paymentRepository.trash(id)
    }

    override fun getAll(pageable: Pageable): Page<PaymentResponse> {
        return paymentRepository.findAllNotDeletedForPageable(pageable).map {
            PaymentResponse.toResponse(it)
        }
    }

}