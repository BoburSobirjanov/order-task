package uz.com.ordertask

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/user")
class UserController(val userService: UserService){

    @PostMapping("create")
    fun create(@RequestBody request: UserCreateRequest)=userService.create(request)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) = userService.getOne(id)

    @PutMapping("{id}")
    fun update(@RequestBody request: UserUpdateRequest, @PathVariable id:Long) = userService.update(id, request)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @GetMapping("get-all")
    fun getAll(pageable: Pageable)=userService.getAll(pageable)

}


@RestController
@RequestMapping("api/v1/category")
class CategoryController(val categoryService: CategoryService){

    @PostMapping("create")
    fun create(@RequestBody request: CategoryCreateRequest)=categoryService.create(request)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long)= categoryService.getOne(id)


    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CategoryUpdateRequest) = categoryService.update(id, request)


    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long)= categoryService.delete(id)
}