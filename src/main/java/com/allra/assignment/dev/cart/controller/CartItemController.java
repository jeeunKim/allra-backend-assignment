package com.allra.assignment.dev.cart.controller;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.cart.service.CartItemService;
import com.allra.assignment.dev.cart.model.request.CartItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Cart API")
public class CartItemController {

    private final CartItemService cartItemService;

    /**
     * @param request
     *          상품 고유번호 NotNull
     *          유저 아이디 NotNull
     *          수량 NotNull, Positive
     * @return 201 응답, 장바구니 조회 URI 헤더 추가
     */
    @Operation(summary = "장바구니 추가 API")
    @PostMapping(value = "/api/cart")
    public ResponseEntity<Void> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        CartItem cartItem = cartItemService.addItemToCart(request);

        URI location = linkTo(methodOn(CartItemController.class).getCartItem(cartItem.getUser().getUserId())).toUri();
        return ResponseEntity.created(location).build();
    }


    /**
     * 임시
     */
    @Operation(summary = "장바구니 조회 API")
    @GetMapping(value = "/api/cart/{userId}")
    public ResponseEntity<Void> getCartItem(@PathVariable Long userId) {

        return ResponseEntity.ok().build();
    }

}
