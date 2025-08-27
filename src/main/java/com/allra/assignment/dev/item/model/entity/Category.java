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
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    // 하위 카테고리
    @OneToMany(mappedBy = "category")
    private List<DetailCategory> detailCategory = new ArrayList<>();

    // 해당 카테고리의 상품리스트
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @Size(max = 30)
    @NotNull
    @Column(name = "category_name", nullable = false, length = 30)
    private String categoryName;

    @Size(max = 20)
    @NotNull
    @Column(name = "category_code", nullable = false, length = 20)
    private String categoryCode;


}