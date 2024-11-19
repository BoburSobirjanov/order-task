package uz.com.ordertask

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
@Service
interface UserService{
    fun create(request:UserCreateRequest)
    fun getOne(id: Long): UserResponse
    fun update(id: Long, request: UserUpdateRequest)
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<UserResponse>
}

@Service
class UserServiceImpl(
        private val userRepository: UserRepository
):UserService {
    override fun create(request: UserCreateRequest) {
        request.run {
            val user  = userRepository.findByUsernameAndDeletedFalse(username)
            if (user!=null)throw UserHasAlreadyExistsException()
            userRepository.save(this.toEntity())
        }
    }

    override fun getOne(id: Long): UserResponse {
        return userRepository.findByIdAndDeletedFalse(id)?.let {
            UserResponse.toResponse(it)
        }?:throw UserNotFoundException()
    }

    override fun update(id: Long, request: UserUpdateRequest) {
        val user = userRepository.findByIdAndDeletedFalse(id)?:throw UserNotFoundException()
        request.run {
           username?.let {
               val usernameAndDeletedFalse = userRepository.findByUsername(id,it)
               if (usernameAndDeletedFalse!=null)throw UserHasAlreadyExistsException()
               user.username=it
           }
           fullName?.let { request.fullName=it }
           email?.let { request.email=it }
           address?.let { request.address=it }
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
interface CategoryService{
    fun create(request:CategoryCreateRequest)
    fun getOne(id: Long): CategoryResponse
    fun update(id: Long, request: CategoryUpdateRequest)
    fun delete(id: Long)
    fun getAll(pageable: Pageable): Page<CategoryResponse>
}

@Service
class CategoryServiceImpl(
        private val categoryRepository: CategoryRepository
):CategoryService{
    override fun create(request: CategoryCreateRequest) {
        request.run {
                val categoryEntity = categoryRepository.findByNameAndDeletedFalse(name)
                if (categoryEntity!=null)throw CategoryHasAlreadyExistsException()
            categoryRepository.save(this.toEntity())
        }
    }

    override fun getOne(id: Long): CategoryResponse {
        return categoryRepository.findByIdAndDeletedFalse(id)?.let {
            CategoryResponse.toResponse(it)
        }?:throw CategoryNotFoundException()
    }

    override fun update(id: Long, request: CategoryUpdateRequest) {
        val categoryEntity = categoryRepository.findByIdAndDeletedFalse(id)?:throw CategoryNotFoundException()
        request.run {
            name?.let {
                val category = categoryRepository.findByName(id,it)
                if (category!=null)throw CategoryHasAlreadyExistsException()
                categoryEntity.name=it
            }
            description?.let { request.description=it }
        }
        categoryRepository.save(categoryEntity)
    }

    override fun delete(id: Long) {
        categoryRepository.trash(id)
    }

    override fun getAll(pageable: Pageable): Page<CategoryResponse> {
        return categoryRepository.findAllNotDeletedForPageable(pageable).map {
            CategoryResponse.toResponse(it)
        }
    }

}