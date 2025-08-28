package com.allra.assignment.dev.item.controller;

import com.allra.assignment.dev.item.model.response.ItemResponse;
import com.allra.assignment.dev.item.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Item API")
public class ItemController {

    private final ItemService itemService;

    /**
     *
     * @param categoryId 카테고리명
     * @param itemName 상품명
     * @param minAmount 최소 가격
     * @param maxAmount 최대 가격
     * @param pageable 페이징 정보
     * @return 페이징 정보를 포함한 상품 목록
     * @Cachable 같은 파라미터 호출을 캐싱
     */
    @Operation(summary = "상품 목록 조회 API")
    @Cacheable("itemPageCache")
    @GetMapping(value = "/api/items")
    public ResponseEntity<PagedModel<EntityModel<ItemResponse>>> getItems(@RequestParam(required = false) Long categoryId,
                                                                          @RequestParam(required = false) Long detailCategoryId,
                                                                          @RequestParam(required = false) String itemName,
                                                                          @RequestParam(required = false) Long minAmount,
                                                                          @RequestParam(required = false) Long maxAmount,
                                                                          Pageable pageable) {

        Page<ItemResponse> itemPage = itemService.getItems(categoryId, detailCategoryId, itemName, minAmount, maxAmount, pageable);

        PagedResourcesAssembler<ItemResponse> itemPageAssembler =
                new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(), null);

        return ResponseEntity.ok()
                    .body(itemPageAssembler
                            .toModel(itemPage, itemResponse ->
                                EntityModel.of(itemResponse, linkTo(methodOn(ItemController.class).getItem(itemResponse.getId())).withSelfRel())
                            )
                    );
    }





    // README 임시
    @Operation(summary = "상품 조회 API")
    @GetMapping(value = "/api/item/{itemId}")
    public ResponseEntity<String> getItem(@PathVariable Long itemId) {

        return ResponseEntity.ok().body("TEMP");
    }
}
