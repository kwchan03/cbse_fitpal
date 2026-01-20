package com.fitpal.api.dtos;

import java.util.List;

public class MealSearchResponse {
    private List<MealSearchResult> data;
    private Pagination pagination;

    public MealSearchResponse() {
    }

    public MealSearchResponse(List<MealSearchResult> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<MealSearchResult> getData() {
        return data;
    }

    public void setData(List<MealSearchResult> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    // Nested Pagination class
    public static class Pagination {
        private Integer total;
        private Integer page;
        private Integer pages;

        public Pagination() {
        }

        public Pagination(Integer total, Integer page, Integer pages) {
            this.total = total;
            this.page = page;
            this.pages = pages;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getPage() {
            return page;
        }

        public void setPage(Integer page) {
            this.page = page;
        }

        public Integer getPages() {
            return pages;
        }

        public void setPages(Integer pages) {
            this.pages = pages;
        }
    }
}