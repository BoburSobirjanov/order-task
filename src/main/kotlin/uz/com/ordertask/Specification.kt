package uz.com.ordertask

import org.springframework.data.jpa.domain.Specification
import java.time.LocalDateTime

object OrderSpecification {

    fun hasUser(userId: Long?): Specification<OrderEntity> {
        return Specification { root, _, criteriaBuilder ->
            userId?.let {
                criteriaBuilder.equal(root.get<Long>("user").get<Long>("id"), it)
            } ?: criteriaBuilder.conjunction()
        }
    }

    fun isCreatedBetween(startTime: LocalDateTime?, endTime: LocalDateTime?): Specification<OrderEntity> {
        return Specification { root, _, criteriaBuilder ->
            when {
                startTime != null && endTime != null -> criteriaBuilder.between(root.get("createdDate"), startTime, endTime)
                startTime != null -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), startTime)
                endTime != null -> criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), endTime)
                else -> criteriaBuilder.conjunction()
            }
        }
    }

    fun isNotDeleted(): Specification<OrderEntity> {
        return Specification { root, _, criteriaBuilder ->
            criteriaBuilder.isFalse(root.get("deleted"))
        }
    }

}
