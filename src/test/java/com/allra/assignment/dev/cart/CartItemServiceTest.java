package com.allra.assignment.dev.cart;

import com.allra.assignment.dev.cart.model.dto.MyItemDto;
import com.allra.assignment.dev.cart.model.entity.CartItem;
import com.allra.assignment.dev.cart.repository.CartItemRepository;
import com.allra.assignment.dev.cart.service.CartItemService;
import com.allra.assignment.dev.item.model.entity.Category;
import com.allra.assignment.dev.item.model.entity.DetailCategory;
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

import java.util.List;
import java.util.Map;
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

    // 장바구니 추가-------------------------------------------------
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


    // 장바구니 조회-------------------------------------------------

    @Test
    @DisplayName("상위 카테고리별 그룹화 및 가격 정보 포함")
    void getCartItems_groupedByCategory() {
        // given
        Long userId = 1L;

        Category category1 = new Category();
        category1.setCategoryId(10);

        DetailCategory detailCategory1 = new DetailCategory();
        detailCategory1.setDetailCategoryId(1);

        Category category2 = new Category();
        category2.setCategoryId(20);

        DetailCategory detailCategory2 = new DetailCategory();
        detailCategory2.setDetailCategoryId(2);

        Item item1 = Item.builder()
                .itemId(101L)
                .name("노트북 A")
                .amount(1000L)
                .discountAmount(800L)
                .discountRate(20.0)
                .isSoldOut(true)
                .category(category1)
                .detailCategory(detailCategory1)
                .build();

        Item item2 = Item.builder()
                .itemId(102L)
                .name("노트북 B")
                .amount(2000L)
                .discountAmount(1800L)
                .discountRate(10.0)
                .isSoldOut(false)
                .category(category2)
                .detailCategory(detailCategory2)
                .build();

        CartItem cartItem1 = CartItem.builder()
                .cartItemId(1L)
                .user(User.builder().userId(userId).build())
                .item(item1)
                .totalAmount(1000L)
                .totalDiscountAmount(800L)
                .discountRate(20.0)
                .quantity(2)
                .build();

        CartItem cartItem2 = CartItem.builder()
                .cartItemId(2L)
                .user(User.builder().userId(userId).build())
                .item(item2)
                .totalAmount(2000L)
                .totalDiscountAmount(1500L)
                .discountRate(25.0)
                .quantity(1)
                .build();

        // Mock repository
        given(cartItemRepository.findByUserUserId(userId))
                .willReturn(List.of(cartItem1, cartItem2));

        // when
        Map<Integer, List<MyItemDto>> result = cartService.getCartItems(userId);

        // then
        MyItemDto dto1 = result.get(10).get(0);
        assertThat(dto1.getItemName()).isEqualTo("노트북 A");
        assertThat(dto1.getAmountAtAdded().getTotalAmount()).isEqualTo(1000L);
        assertThat(dto1.getAmountNow().getTotalAmount()).isEqualTo(1000L * 2);
        assertThat(dto1.getItemLocation()).isNotNull();

        MyItemDto dto2 = result.get(20).get(0);
        assertThat(dto2.getItemName()).isEqualTo("노트북 B");
        assertThat(dto2.getAmountAtAdded().getTotalAmount()).isEqualTo(2000L);
        assertThat(dto2.getAmountNow().getTotalAmount()).isEqualTo(2000L * 1);
        assertThat(dto2.getItemLocation()).isNotNull();
    }




}
