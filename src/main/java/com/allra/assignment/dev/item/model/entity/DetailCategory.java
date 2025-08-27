package com.allra.assignment.dev.item.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "detail_categories")
public class DetailCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_category_id", nullable = false)
    private Integer detailCategoryId;

    // 상위 카테고리
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    private Category category;

    // 해당 카테고리의 상품리스트
    @OneToMany(mappedBy = "detailCategory", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @Size(max = 30)
    @NotNull
    @Column(name = "detail_category_name", nullable = false, length = 30)
    private String detailCategoryName;

    @Size(max = 20)
    @NotNull
    @Column(name = "detail_category_code", nullable = false, length = 20)
    private String categoryCode;


}