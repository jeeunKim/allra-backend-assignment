package com.allra.assignment.dev.item.repository;

import com.allra.assignment.dev.item.model.response.ItemResponse;
import com.allra.assignment.dev.item.model.entity.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
        select new com.allra.assignment.dev.item.model.response.ItemResponse(i) 
        from Item i
        where (:categoryId is null or i.category.categoryId = :categoryId )
        	  and (:detailCategoryId is null or i.detailCategory.detailCategoryId = :detailCategoryId)
          	  and (:itemName is null or i.name like concat('%', :itemName, '%'))
        	  and (:minAmount is null or i.discountAmount >= :minAmount)
        	  and (:maxAmount is null or i.discountAmount <= :maxAmount)
    """)
    Page<ItemResponse> findItems(Long categoryId, Long detailCategoryId, String itemName, Long minAmount, Long maxAmount, Pageable pageable);


    // 행 단위 Exclusive Lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.itemId = :itemId")
    Optional<Item> findByIdWithPessimisticLock(@Param("itemId") Long itemId);
}