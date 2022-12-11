package com.spring.cloud.recommend.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "recommends")
@CompoundIndex(name = "prod-rec-id", unique = true, def = "{'productId': 1, 'recommendId': 1}")
public class RecommendEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int productId;
    private int recommendId;
    private String author;
    private String content;

    public RecommendEntity(int productId, int recommendId, String author, String content) {
        this.productId = productId;
        this.recommendId = recommendId;
        this.author = author;
        this.content = content;
    }
}
