package com.spring.cloud.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ServiceAddresses {
    private String composite;
    private String product;
    private String recommend;
    private String review;
}
