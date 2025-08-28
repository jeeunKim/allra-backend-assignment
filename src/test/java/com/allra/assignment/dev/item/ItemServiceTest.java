package com.allra.assignment.dev.item;

import com.allra.assignment.exception.result.ItemErrorResult;
import com.allra.assignment.exception.custom.ItemException;
import com.allra.assignment.dev.item.model.response.ItemResponse;
import com.allra.assignment.dev.item.repository.ItemRepository;
import com.allra.assignment.dev.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;


    @Test
    @DisplayName("정렬이 올바르게 된다")
    void sortItemPage() {
        // given
        ItemResponse item1 = ItemResponse.builder()
                .discountAmount(2000L)
                .build();

        ItemResponse item2 = ItemResponse.builder()
                .discountAmount(1000L)
                .build();

        Page<ItemResponse> page = new PageImpl<>(List.of(item1, item2));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "discountAmount"));

        given(itemRepository.findItems(anyLong(), anyLong(), anyString(), anyLong(), anyLong(), eq(pageable)))
                .willReturn(page);

        // when
        Page<ItemResponse> result =
                itemService.getItems(1L, 1L, "노트북", 0L, 3000L, pageable);

        // then
        List<ItemResponse> content = result.getContent()
                .stream()
                .toList();

        assertThat(content).hasSize(2);
        assertThat(content.get(0).getDiscountAmount()).isGreaterThanOrEqualTo(content.get(1).getDiscountAmount());
    }


    @Test
    @DisplayName("최소 가격이 최대 금액보다 커서는 안된다")
    void maxGreaterThanEqualToMin() {
        // given
        Long minAmount = 5000L;
        Long maxAmount = 3000L;
        Long categoryId = 1L;
        Long detailCategoryId = 1L;
        String itemName = "노트북";
        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThatThrownBy(() ->itemService.getItems(categoryId, detailCategoryId, itemName, minAmount, maxAmount, pageable))
                .isInstanceOf(ItemException.class)
                .extracting("errorResult")
                .isEqualTo(ItemErrorResult.INVALID_AMOUNT_RANGE);
    }


    @Test
    @DisplayName("상위, 하위 카테고리, 이름, 가격 범위로 상품 조회")
    void searchItemsWithAllFilters() {
        // given
        Long categoryId = 1L;
        Long detailCategoryId = 10L;
        String itemName = "노트북";
        Long minAmount = 1000000L;
        Long maxAmount = 1500000L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "amount"));

        // 테스트용 ItemDto 생성
        ItemResponse item1 = ItemResponse.builder()
                .id(100L)
                .categoryName("전자기기")
                .detailCategoryName("노트북")
                .itemName("노트북 A")
                .amount(1000000L)
                .discountAmount(900000L)
                .build();

        ItemResponse item2 = ItemResponse.builder()
                .id(101L)
                .categoryName("전자기기")
                .detailCategoryName("노트북")
                .itemName("노트북 B")
                .amount(1200000L)
                .discountAmount(1100000L)
                .build();

        Page<ItemResponse> page = new PageImpl<>(List.of(item2));

        // Repository mock
        given(itemRepository.findItems(
                eq(categoryId),
                eq(detailCategoryId),
                eq(itemName),
                eq(minAmount),
                eq(maxAmount),
                eq(pageable)
        )).willReturn(page);

        // when
        Page<ItemResponse> result = itemService.getItems(categoryId, detailCategoryId, itemName, minAmount, maxAmount, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);

        Optional<ItemResponse> firstItem = result.getContent().stream().findFirst();
        assertThat(firstItem.get().getItemName()).isEqualTo("노트북 B");
    }


}
