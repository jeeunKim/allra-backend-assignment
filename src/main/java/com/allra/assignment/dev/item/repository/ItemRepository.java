package com.allra.assignment.dev.item.repository;

import com.allra.assignment.dev.item.model.dto.ItemDto;
import com.allra.assignment.dev.item.model.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
        select new com.allra.assignment.dev.item.model.dto.ItemDto(i) 
        from Item i
        where (:categoryId is null or i.category.categoryId = :categoryId )
        	  and (:detailCategoryId is null or i.detailCategory.detailCategoryId = :detailCategoryId)
          	  and (:itemName is null or i.name like concat('%', :itemName, '%'))
        	  and (:minAmount is null or i.discountAmount >= :minAmount)
        	  and (:maxAmount is null or i.discountAmount <= :maxAmount)
    """)
    Page<ItemDto> findItems(Long categoryId, Long detailCategoryId, String itemName, Long minAmount, Long maxAmount, Pageable pageable);
}