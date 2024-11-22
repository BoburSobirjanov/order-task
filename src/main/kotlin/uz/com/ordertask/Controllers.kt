package uz.com.ordertask

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ExceptionHandler(private val errorMessageSource: ResourceBundleMessageSource) {

    @ExceptionHandler(DemoExceptionHandler::class)
    fun handleAccountException(exception: DemoExceptionHandler): ResponseEntity<BaseMessage> {
        return ResponseEntity.badRequest().body(exception.getErrorMessage(errorMessageSource))
    }
}

@RestController
@RequestMapping("api/v1/user")
class UserController(val userService: UserService) {

    @PostMapping("create")
    fun create(@RequestBody request: UserCreateRequest) = userService.create(request)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) = userService.getOne(id)

    @PutMapping("{id}")
    fun update(@RequestBody request: UserUpdateRequest, @PathVariable id: Long) = userService.update(id, request)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = userService.delete(id)

    @GetMapping("get-all")
    fun getAll(pageable: Pageable) = userService.getAll(pageable)

}

@RestController
@RequestMapping("api/v1/category")
class CategoryController(val categoryService: CategoryService) {

    @PostMapping("create")
    fun create(@RequestBody request: CategoryCreateRequest) = categoryService.create(request)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) = categoryService.getOne(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CategoryUpdateRequest) = categoryService.update(id, request)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = categoryService.delete(id)

    @GetMapping("get-all")
    fun getAll(pageable: Pageable) = categoryService.getAll(pageable)
}

@RestController
@RequestMapping("api/v1/product")
class ProductController(val productService: ProductService) {
    @PostMapping("create")
    fun create(@RequestBody request: ProductCreateRequest) = productService.create(request)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) = productService.getOne(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ProductUpdateRequest) = productService.update(id, request)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = productService.delete(id)

    @GetMapping("get-all")
    fun getAll(pageable: Pageable) = productService.getAll(pageable)

    @GetMapping("{id}/get-product-users")
    fun getProductUsers(@PathVariable id:Long, pageable: Pageable)=productService.getUserCountForProduct(id, pageable)
}

@RestController
@RequestMapping("api/v1/order")
class OrderController(val orderService: OrderService){

    @PostMapping("create")
    fun create(@RequestBody request:OrderCreateRequest)=orderService.create(request)

    @PutMapping("{id}/cancel")
    fun cancel(@PathVariable id: Long, @RequestParam userId:Long)=orderService.cancel(id, userId)

    @GetMapping("get-user-orders")
    fun getUserOrders(@RequestParam userId: Long, pageable: Pageable)=orderService.getUserOrders(userId, pageable)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long)=orderService.getOne(id)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long)=orderService.delete(id)

    @GetMapping("get-all")
    fun getAll(@RequestParam(required = false) startTime : String?,
               @RequestParam(required = false) endTime : String? ,
               @RequestParam(required = false) userId: Long?, pageable: Pageable)= orderService.getAll(userId, startTime, endTime, pageable)
}


@RestController
@RequestMapping("api/v1/order-item")
class OrderItemController(val orderItemService: OrderItemService){

    @PostMapping("create")
    fun create(@RequestBody request: OrderItemsCreateRequest)=orderItemService.create(request)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long)=orderItemService.getOne(id)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long)=orderItemService.delete(id)

    @GetMapping("get-all")
    fun getAll(pageable: Pageable)=orderItemService.getAll(pageable)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody request:OrderItemsUpdateRequest)=orderItemService.update(id, request)
}

@RestController
@RequestMapping("api/v1/payment")
class PaymentController(val paymentService: PaymentService){

    @PostMapping("create")
    fun create(@RequestBody request: PaymentCreateRequest)=paymentService.create(request)
    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long)=paymentService.getOne(id)
    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long)=paymentService.delete(id)
    @GetMapping("get-all")
    fun getAll(pageable: Pageable)=paymentService.getAll(pageable)
    @GetMapping("get-user-payments")
    fun getUserPayments(@RequestParam userId: Long, pageable: Pageable)=paymentService.getUserPayments(userId, pageable)
}