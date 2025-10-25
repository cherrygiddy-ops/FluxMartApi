package com.fluxmartApi.order;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderService orderService;
    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderDetailsForCustomer (@PathVariable("orderId") Integer orderId){
        return orderService.getOrderDetailsForCustomer(orderId);
    }

    @GetMapping()
    public List<OrderResponseDto> getAllOrdersForCustomer (){
        return orderService.getAllOrderForCustomer();
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?>handleOrderNotFound(){
        return ResponseEntity.notFound().build();
    }


}
