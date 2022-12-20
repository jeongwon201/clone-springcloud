package com.spring.cloud.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Composite {
    private int productId;
    private String productName;
    private List<Recommend> recommendList;
    private List<Review> reviewList;
    private ServiceAddresses serviceAddresses;
}