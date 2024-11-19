package uz.com.ordertask

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrderTaskApplication

fun main(args: Array<String>) {
    runApplication<OrderTaskApplication>(*args)
}
