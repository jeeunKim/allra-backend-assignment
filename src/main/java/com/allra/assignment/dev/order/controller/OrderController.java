package com.allra.assignment.dev.order.controller;

import com.allra.assignment.dev.order.model.response.OrderResponse;
import com.allra.assignment.dev.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Order API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 결제 API",
            description = """
                          장바구니 내에 모든 상품에 대해 주문을 처리합니다.
                          주문 시 상품의 재고를 관리합니다.
                          주문 내역, 주문 상세, 결제 이력을 관리합니다.
                          """
    )
    @PostMapping(value = "/api/payment")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody Long userId) {

        return ResponseEntity.ok().body(orderService.createOrder(userId));
    }
}
