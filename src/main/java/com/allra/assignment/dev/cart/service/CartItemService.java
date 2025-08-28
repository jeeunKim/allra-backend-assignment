package com.allra.assignment.dev.cart.service;

import com.allra.assignment.dev.item.model.dto.ItemAmountDto;
import com.allra.assignment.dev.cart.model.dto.MyItemDto;
import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.cart.repository.CartItemRepository;
import com.allra.assignment.dev.item.controller.ItemController;
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

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    /**
     * 장바구니 조회 로직
     * 1. 품절 상태
     * 2. 상위 카테고리로 그룹화
     * 3. 장바구니에 담을 당시와 현재 상품의 가격 비교
     */
    @Transactional(readOnly = true)
    public Map<Integer, List<MyItemDto>> getCartItems(Long userId) {

        // 장바구니 목록 조회
        return
            cartItemRepository.findByUserUserId(userId)
                .stream()
                .collect(Collectors.groupingBy(
                        // 상위 카테고리로 매핑
                        cartItem -> cartItem.getItem().getCategory().getCategoryId(),
                        // 해당 카테고리 장바구니 상품 DTO 변환
                        Collectors.mapping(cartItem -> {
                            // 상품 정보 DTO 변환
                            MyItemDto myItemDto = new MyItemDto(cartItem);

                            // 담을 당시 상품 금액
                            myItemDto.setAmountAtAdded(new ItemAmountDto(cartItem));
                            // 현재 상품의 가격
                            myItemDto.setAmountNow(new ItemAmountDto(cartItem.getItem(), cartItem.getQuantity()));

                            // 상품 조회 URI
                            URI location = linkTo(methodOn(ItemController.class).getItem(cartItem.getItem().getItemId())).toUri();
                            myItemDto.setItemLocation(location);

                            return myItemDto;
                        }, Collectors.toList())
                ));

    }


    /**
     * 장바구니 상품 수량 조정
     * @param isIncrement True == 증가, False == 감소
     */
    @Transactional
    public CartItem modifyQuantity(Long userId, Long itemId, boolean isIncrement) {

        return cartItemRepository.findByUserUserIdAndItemItemId(userId, itemId)
                .map(cartItem -> {
                    // 품절 체크
                    if(cartItem.getItem().getIsSoldOut()) {
                        throw new CartItemException(CartItemErrorResult.CANNOT_MODIFY_IS_SOLD_OUT);
                    }
                    // 수량 증/감
                    if(isIncrement) {
                        incrementQuantity(cartItem);
                    } else {
                        decrementQuantity(cartItem);
                    }

                    return cartItem;
                }).orElseThrow(() -> new CartItemException(CartItemErrorResult.ITEM_NOT_IN_CART));

    }


    // 수량 +1
    private void incrementQuantity(CartItem cartItem) {
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        int quantity = cartItem.getQuantity();

        // 재고 체크
        if(quantity > cartItem.getItem().getStock()) {
            throw new CartItemException(CartItemErrorResult.CANNOT_MODIFY_NOT_ENOUGH_STOCK);
        }

        // 수량에 따른 금액 변경 (담았을 시에 가격이 아닌 현재 가격을 기반으로 계산)
        cartItem.setTotalAmount(cartItem.getItem().getAmount() * quantity);
        cartItem.setTotalDiscountAmount(cartItem.getItem().getDiscountAmount() * quantity);
        cartItem.setDiscountRate(cartItem.getItem().getDiscountRate());
        cartItemRepository.save(cartItem);
    }

    // 수량 -1
    private void decrementQuantity(CartItem cartItem) {
        // 감소하여 수량이 없어지는 경우
        if(cartItem.getQuantity() - 1 == 0) {
            cartItemRepository.delete(cartItem);
        }
        // 감소만 하는 경우
        else {
            cartItem.setQuantity(cartItem.getQuantity() - 1);

            int quantity = cartItem.getQuantity();
            // 수량에 따른 금액 변경 (담았을 시에 가격이 아닌 현재 가격을 기반으로 계산)
            cartItem.setTotalAmount(cartItem.getItem().getAmount() * quantity);
            cartItem.setTotalDiscountAmount(cartItem.getItem().getDiscountAmount() * quantity);
            cartItem.setDiscountRate(cartItem.getItem().getDiscountRate());
            cartItemRepository.save(cartItem);
        }
    }


}
