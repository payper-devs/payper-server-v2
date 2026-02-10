package com.payper.server.merchant.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 카테고리명
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 부모 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    /**
     * 부모 카테고리인가?
     */
    public boolean isRoot() {
        return parentCategory == null;
    }

    /**
     * 자식 카테고리인가?
     */
    public boolean isDepth2() {
        return parentCategory != null;
    }

    /**
     * 카테고리 등록
     */
    public static Category register(String name, Category parentCategory) {
        return Category.builder()
                .name(name)
                .parentCategory(parentCategory)
                .build();
    }

    /**
     * 이름 AND 부모 변경
     */
    public void updateNameAndParentCategory(String name, Category parentCategory) {
        this.name = name;
        this.parentCategory = parentCategory;
    }

    /**
     * 이름 변경
     */
    public void updateName(String name) {
        this.name = name;
    }
}