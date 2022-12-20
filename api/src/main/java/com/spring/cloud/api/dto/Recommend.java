package com.spring.cloud.api.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Recommend {
    private int productId;
    private int recommendId;
    private String author;
    private String content;
    private String serviceAddress;
}
