package com.allra.assignment.dev.cart;

import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.cart.repository.CartItemRepository;
import com.allra.assignment.dev.cart.service.CartItemService;
import com.allra.assignment.dev.item.model.entity.Item;
import com.allra.assignment.dev.cart.model.request.CartItemRequest;
import com.allra.assignment.dev.item.repository.ItemRepository;
import com.allra.assignment.dev.user.model.entity.User;
import com.allra.assignment.dev.user.repository.UserRepository;
import com.allra.assignment.exception.result.CartItemErrorResult;
import com.allra.assignment.exception.custom.CartItemException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartItemService cartService;


    @Test
    @DisplayName("장바구니에 상품이 이미 존재하면 수량과 금액이 갱신된다")
    void addItemToCartWhenExist() {
        // given
        Item item = Item.builder()
                .itemId(1L)
                .amount(1000L)
                .discountAmount(800L)
                .discountRate(20.0)
                .build();

        CartItem existingCartItem = CartItem.builder()
                .cartItemId(1L)
                .item(item)
                .quantity(2)
                .totalAmount(2000L)
                .totalDiscountAmount(1600L)
                .discountRate(20.0)
                .build();

        CartItemRequest request = new CartItemRequest();
        request.setQuantity(3);
        request.setUserId(1);
        request.setItemId(1);

        given(cartItemRepository.findByUserUserIdAndItemItemId(anyLong(), anyLong()))
                .willReturn(Optional.of(existingCartItem));

        given(cartItemRepository.save(any(CartItem.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        CartItem result = cartService.addItemToCart(request);

        // then
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getTotalAmount()).isEqualTo(5000);
        assertThat(result.getTotalDiscountAmount()).isEqualTo(4000);
    }

    @Test
    @DisplayName("장바구니에 처음 담는 경우 새로운 CartItem이 생성된다")
    void addItemToCartWhenNotExist() {
        // given
        Item item = Item.builder()
                .itemId(1L)
                .amount(1000L)
                .discountAmount(800L)
                .discountRate(20.0)
                .build();

        User user = User.builder()
                .userId(1L)
                .build();

        CartItemRequest request = new CartItemRequest();
        request.setQuantity(2);
        request.setUserId(1);
        request.setItemId(1);

        given(cartItemRepository.findByUserUserIdAndItemItemId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        given(itemRepository.findById(1L)).willReturn(Optional.of(item));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(cartItemRepository.save(any(CartItem.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        CartItem result = cartService.addItemToCart(request);

        // then
        assertThat(result.getItem()).isEqualTo(item);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getTotalAmount()).isEqualTo(2000);
        assertThat(result.getTotalDiscountAmount()).isEqualTo(1600);
        assertThat(result.getDiscountRate()).isEqualTo(20.0);
    }


    @Test
    @DisplayName("품절 상품은 장바구니에 담을 수 없다.")
    void soldOutItemCannotAdd() {
        // given
        Item item = Item.builder()
                .itemId(1L)
                .amount(1000L)
                .discountAmount(800L)
                .discountRate(20.0)
                .isSoldOut(true)
                .build();

        CartItemRequest request = new CartItemRequest();
        request.setQuantity(2);
        request.setUserId(1);
        request.setItemId(1);

        given(cartItemRepository.findByUserUserIdAndItemItemId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        given(itemRepository.findById(1L)).willReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> cartService.addItemToCart(request))
                .isInstanceOf(CartItemException.class)
                .extracting("errorResult")
                .isEqualTo(CartItemErrorResult.IS_SOLD_OUT_ITEM);
    }


    @Test
    @DisplayName("남은 재고 이상 장바구니에 담을 수 없다.")
    void notEnoughStockItemCannotAdd() {
        // given
        Item item = Item.builder()
                .itemId(1L)
                .amount(1000L)
                .discountAmount(800L)
                .discountRate(20.0)
                .isSoldOut(false)
                .stock(1L)
                .build();

        CartItemRequest request = new CartItemRequest();
        request.setQuantity(2);
        request.setUserId(1);
        request.setItemId(1);

        given(cartItemRepository.findByUserUserIdAndItemItemId(anyLong(), anyLong()))
                .willReturn(Optional.empty());
        given(itemRepository.findById(1L)).willReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> cartService.addItemToCart(request))
                .isInstanceOf(CartItemException.class)
                .extracting("errorResult")
                .isEqualTo(CartItemErrorResult.NOT_ENOUGH_STOCK);
    }


}
