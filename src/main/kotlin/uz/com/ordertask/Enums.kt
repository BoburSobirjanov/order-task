package uz.com.ordertask


enum class ErrorCodes(val code:Int){

    USER_HAS_ALREADY_EXISTS_EXCEPTION(400),
    USER_NOT_FOUND_EXCEPTION(401),
    CATEGORY_HAS_ALREADY_EXISTS_EXCEPTION(300),
    CATEGORY_NOT_FOUND_EXCEPTION(301)

}

enum class OrderStatus{
    PENDING, DELIVERED, FINISHED, CANCELLED
}

enum class PaymentMethod{
    HUMO, UZCARD, PAYME, CASH
}

enum class UserRole{
    USER,ADMIN
}