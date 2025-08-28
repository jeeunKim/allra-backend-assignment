package com.allra.assignment.dev.cart.controller;

import com.allra.assignment.dev.cart.model.dto.MyItemDto;
import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.cart.model.response.CartItemResponse;
import com.allra.assignment.dev.cart.service.CartItemService;
import com.allra.assignment.dev.cart.model.request.CartItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

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
    @Operation(summary = "장바구니 추가 API",
               description = """
                            상품번호, 유저아이디, 수량을 필수 요청받습니다.
                            장바구니 존재여부에 따라 수량 증가/새로 생성으로 나뉩니다.
                            품절 및 재고 상태에 대한 예외처리를 포함합니다.
                             """
    )
    @CrossOrigin(exposedHeaders = "Location")
    @PostMapping(value = "/api/cart")
    public ResponseEntity<Void> addItemToCart(@Valid @RequestBody CartItemRequest request) {
        CartItem cartItem = cartItemService.addItemToCart(request);

        URI location = linkTo(methodOn(CartItemController.class).getCartItems(cartItem.getUser().getUserId())).toUri();
        return ResponseEntity.created(location).build();
    }


    /**
     * 장바구니 조회 API
     * @return 상위 카테고리별로 묶은 상품 정보와 총 금액, 수량 정보
     */
    @Operation(summary = "장바구니 조회 API",
               description = """
                             장바구니 목록을 상위 카테고리 별로 그룹화하여 조회합니다.
                             장바구니에 넣을 당시 금액과 현재 상품의 가격을 모두 포함합니다.
                             품절 상태를 포함합니다.
                             상품 조회 URI 포함합니다.                       
                             """
    )
    @GetMapping(value = "/api/cart/{userId}")
    public ResponseEntity<Map<Integer, List<MyItemDto>>> getCartItems(@PathVariable Long userId) {


        return ResponseEntity.ok().body(cartItemService.getCartItems(userId));
    }

    /**
     * 장바구니 수정 API
     */
    @Operation(summary = "장바구니 수정 API",
            description = "장바구니에 담은 상품의 수량 수정"
    )
    @CrossOrigin(exposedHeaders = "Location")
    @PatchMapping(value = "/api/cart/{userId}/{itemId}")
    public ResponseEntity<Void> modifyQuantity(@PathVariable Long userId,
                                               @PathVariable Long itemId,
                                               @RequestBody boolean isIncrement) {

        CartItem cartItem = cartItemService.modifyQuantity(userId, itemId, isIncrement);
        URI location = linkTo(methodOn(CartItemController.class).getCartItems(cartItem.getUser().getUserId())).toUri();

        return ResponseEntity.status(HttpStatus.OK).header("location", String.valueOf(location)).build();
    }




}
