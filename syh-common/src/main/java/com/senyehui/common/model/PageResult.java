package com.senyehui.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 统一分页响应结果
 */
@Data
public class PageResult<T> implements Serializable {

    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 每页条数
     */
    private long pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long pageNum, long pageSize, long total, List<T> records) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.records = records;
        this.pages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(1, 10, 0, Collections.emptyList());
    }
}
