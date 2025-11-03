package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponse {

    private Long id;
    private String name;
    private String description;
    private String creatorName;
}
