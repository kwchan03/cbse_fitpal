package com.fitpal.fitpalspringbootapp.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealSearchResponse {
    private List<MealSearchResult> data;
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private Integer total;
        private Integer page;
        private Integer pages;
    }
}