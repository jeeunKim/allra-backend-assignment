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
import org.springframework.data.web.PageableDefault;
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
     * @param categoryId 상위 카테고리명
     * @param detailCategoryId 하위 카테고리명
     * @param itemName 상품명
     * @param minAmount 최소 가격
     * @param maxAmount 최대 가격
     * @param pageable 페이징 정보
     * @return 페이징 정보를 포함한 상품 목록
     * @Cachable 같은 파라미터 호출을 캐싱, 상품상태 변경에 따라 @CacheEvict 필요
     */
    @Operation(summary = "상품 목록 조회 API",
               description = """
                             상/하위 카테고리, 상품명, 가격 범위로 검색이 가능하며 페이징 정보(기본 사이즈 10)를 포함합니다.
                             각 상품요소는 Self-Link 정보를 갖고 있습니다.
                             정렬은 Client에 의존하며, 정렬을 지원하지 않는 필드에 대한 예외처리를 포합합니다.
                             """
    )
    @Cacheable("itemPageCache")
    @GetMapping(value = "/api/items")
    public ResponseEntity<PagedModel<EntityModel<ItemResponse>>> getItems(@RequestParam(required = false) Long categoryId,
                                                                          @RequestParam(required = false) Long detailCategoryId,
                                                                          @RequestParam(required = false) String itemName,
                                                                          @RequestParam(required = false) Long minAmount,
                                                                          @RequestParam(required = false) Long maxAmount,
                                                                          @PageableDefault(size = 10) final Pageable pageable) {

        Page<ItemResponse> itemPage = itemService.getItems(categoryId, detailCategoryId, itemName, minAmount, maxAmount, pageable);

        PagedResourcesAssembler<ItemResponse> itemPageAssembler =
                new PagedResourcesAssembler<>(new HateoasPageableHandlerMethodArgumentResolver(), null);

        log.info("/api/items Request");
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

        log.info("/api/item/{} Request", itemId);
        return ResponseEntity.ok().body("TEMP");
    }
}
