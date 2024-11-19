package uz.com.ordertask

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
        @CreationTimestamp var createdDate: LocalDateTime? = null,
        @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)

@Entity(name = "users")
class UserEntity(
        @Column(nullable = false) var fullName: String,
        @Column(nullable = false, unique = true) var username: String,
        @Column(nullable = false, unique = true) var email: String,
        @Column(nullable = false) var address: String,
        @Column(nullable = false)  @Enumerated(EnumType.STRING) var userRole: UserRole
) : BaseEntity()

@Entity(name = "categories")
class CategoryEntity(
        @Column(nullable = false, unique = true) var name: String,
        @Column(nullable = false) var description: String,
) : BaseEntity()

@Entity(name = "products")
class ProductEntity(
        @Column(nullable = false) val name: String,
        @Column(nullable = false) val description: String,
        @Column(nullable = false) var price: BigDecimal,
        @Column(nullable = false) var stockCount: Int,
        @ManyToOne var category: CategoryEntity
) : BaseEntity()

@Entity(name = "orders")
class OrderEntity(
        @ManyToOne var user: UserEntity,
        @Column(nullable = false) @Enumerated(EnumType.STRING) var orderStatus: OrderStatus,
        var totalAmount: BigDecimal
) : BaseEntity()

@Entity(name = "payments")
class PaymentEntity(
        @OneToOne var order: OrderEntity,
        @ManyToOne var user: UserEntity,
        @Enumerated(EnumType.STRING) var paymentMethod: PaymentMethod,
        var amount: BigDecimal
) : BaseEntity()

@Entity(name = "order_items")
class OrderItemEntity(
        @ManyToOne var order: OrderEntity,
        @ManyToOne var product: ProductEntity,
        @Column(nullable = false) var quantity: Int,
        @Column(nullable = false) var unitPrice: BigDecimal,
        @Column(nullable = false) var totalPrice: BigDecimal
) : BaseEntity()