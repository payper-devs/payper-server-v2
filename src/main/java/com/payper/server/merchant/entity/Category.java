package com.payper.server.merchant.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 카테고리명
     */
    @Column(nullable = false)
    private String name;

    /**
     * 부모 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    /**
     * 카테고리 이미지 URL
     * TODO: 카테고리 등록할 때 무조건 이미지 url 넣도록 하고 만약 넣지 않는다면 default 이미지를 보여주도록 함
     */
    @Column(name = "image_url")
    private String imageUrl;
}