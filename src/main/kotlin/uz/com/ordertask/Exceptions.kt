package uz.com.ordertask

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

sealed class DemoExceptionHandler : RuntimeException() {
    abstract fun errorCode(): ErrorCodes
    open fun getAllArguments(): Array<Any?>? = null

    fun getErrorMessage(resourceBundle: ResourceBundleMessageSource): BaseMessage {
        val message = try {
            resourceBundle.getMessage(
                    errorCode().name, getAllArguments(), LocaleContextHolder.getLocale()
            )
        } catch (e: Exception) {
            e.message
        }
        return BaseMessage(errorCode().code, message)
    }
}

class UserHasAlreadyExistsException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_HAS_ALREADY_EXISTS_EXCEPTION
}

class UserNotFoundException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_NOT_FOUND_EXCEPTION
}

class CategoryHasAlreadyExistsException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.CATEGORY_HAS_ALREADY_EXISTS_EXCEPTION
}

class CategoryNotFoundException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.CATEGORY_NOT_FOUND_EXCEPTION
}

class ProductNotFoundException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_NOT_FOUND_EXCEPTION
}

class OrderNotFoundException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.ORDER_NOT_FOUND_EXCEPTION
}

class ProductHasNoEnough : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PRODUCT_HAS_NOT_ENOUGH
}

class UserBadRequestException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.USER_BAD_REQUEST_EXCEPTION
}

class PaymentNotFoundException : DemoExceptionHandler() {
    override fun errorCode() = ErrorCodes.PAYMENT_NOT_FOUND_EXCEPTION
}

class OrderItemNotFoundException:DemoExceptionHandler(){
    override fun errorCode()= ErrorCodes.ORDER_ITEM_NOT_FOUND_EXCEPTION
}