package com.payper.server.merchant.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Merchant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 가맹점명
     */
    @Column(nullable = false)
    private String name;

    /**
     * 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * 가맹점 이미지 URL
     * TODO: 가맹점 등록할 때 무조건 이미지 url 넣도록 하고 만약 넣지 않는다면 default 이미지를 보여주도록 함
     */
    @Column(name = "image_url")
    private String imageUrl;
}