package com.allra.assignment.dev.cart.service;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.cart.repository.CartItemRepository;
import com.allra.assignment.dev.item.model.entity.Item;
import com.allra.assignment.dev.cart.model.request.CartItemRequest;
import com.allra.assignment.dev.item.repository.ItemRepository;
import com.allra.assignment.dev.user.model.entity.User;
import com.allra.assignment.dev.user.repository.UserRepository;
import com.allra.assignment.exception.result.CartItemErrorResult;
import com.allra.assignment.exception.result.ItemErrorResult;
import com.allra.assignment.exception.result.UserErrorResult;
import com.allra.assignment.exception.custom.CartItemException;
import com.allra.assignment.exception.custom.ItemException;
import com.allra.assignment.exception.custom.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;


    /**
     * 장바구니 등록 로직
     * 1. 장바구니에 이미 존재 -> 기존 장바구니의 수량 변경(상품의 금액은 장바구니 추가 시점으로 재계산)
     * 2. 신규 상품 -> 요청대로 등록
     * 3. 품절 여부 및 재고 확인
     */
    @Transactional
    public CartItem addItemToCart(CartItemRequest request) {

        return cartItemRepository.findByUserUserIdAndItemItemId(request.getUserId(), request.getItemId())
            // 이미 상품이 존재하는 경우
            .map(cartItem -> {
                Item item = cartItem.getItem();
                int newQuantity = cartItem.getQuantity() + request.getQuantity();

                // 품절 확인
                this.isSoldOut(item, request.getQuantity());

                // 장바구니에 같은 상품 등록시 기존 상품의 가격이 변화가 있을 경우를 대비하여 현재 가격으로 재계산
                cartItem.setTotalAmount(item.getAmount() * newQuantity);
                cartItem.setTotalDiscountAmount(item.getDiscountAmount() * newQuantity);
                cartItem.setDiscountRate(item.getDiscountRate());
                cartItem.setQuantity(newQuantity);

                return cartItemRepository.save(cartItem);
            })
            // 최초로 장바구니에 담는 경우
            .orElseGet(() -> {
                Item item = itemRepository.findById(request.getItemId())
                        .orElseThrow(() -> new ItemException(ItemErrorResult.ITEM_NOT_FOUND));

                // 품절 확인
                this.isSoldOut(item, request.getQuantity());

                User user = userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

                return cartItemRepository.save(CartItem.builder()
                        .item(item)
                        .user(user)
                        .quantity(request.getQuantity())
                        .totalAmount(item.getAmount() * request.getQuantity())
                        .discountRate(item.getDiscountRate())
                        .totalDiscountAmount(item.getDiscountAmount() * request.getQuantity())
                        .build());
            });

    }

    private void isSoldOut(Item item, int quantity) {
        // 품절 체크
        if(Boolean.TRUE.equals(item.getIsSoldOut())) {
            throw new CartItemException(CartItemErrorResult.IS_SOLD_OUT_ITEM);
        }

        // 재고 체크
        if(item.getStock() < quantity) {
            throw new CartItemException(CartItemErrorResult.NOT_ENOUGH_STOCK);
        }

    }


}
