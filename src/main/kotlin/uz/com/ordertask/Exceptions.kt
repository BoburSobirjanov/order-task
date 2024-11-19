package uz.com.ordertask

sealed class DemoExceptionHandler : RuntimeException() {
    abstract fun errorCode(): ErrorCodes
}

class UserHasAlreadyExistsException:DemoExceptionHandler(){
    override fun errorCode()= ErrorCodes.USER_HAS_ALREADY_EXISTS_EXCEPTION
}

class UserNotFoundException:DemoExceptionHandler(){
    override fun errorCode()= ErrorCodes.USER_NOT_FOUND_EXCEPTION
}

class CategoryHasAlreadyExistsException:DemoExceptionHandler(){
    override fun errorCode()= ErrorCodes.CATEGORY_HAS_ALREADY_EXISTS_EXCEPTION
}

class CategoryNotFoundException:DemoExceptionHandler(){
    override fun errorCode()= ErrorCodes.CATEGORY_NOT_FOUND_EXCEPTION
}