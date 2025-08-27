package com.allra.assignment.dev.item.service;

import com.allra.assignment.dev.exception.ItemErrorResult;
import com.allra.assignment.dev.exception.custom.ItemException;
import com.allra.assignment.dev.item.model.dto.ItemDto;
import com.allra.assignment.dev.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    /**
     * 상위 카테고리, 하위 카테고리, 상품명, 최소/대 금액으로 상품 목록 조회
     * @throws ItemException 최소금액이 최대금액보다 큰 경우
     */
    @Transactional(readOnly = true)
    public Page<ItemDto> getItems(Long categoryId, Long detailCategoryId, String itemName, Long minAmount, Long maxAmount, Pageable pageable) {

        // 최소 > 최대일 경우
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            throw new ItemException(ItemErrorResult.INVALID_AMOUNT_RANGE);
        }

        return itemRepository.findItems(categoryId, detailCategoryId, itemName, minAmount, maxAmount, pageable);
    }

}
