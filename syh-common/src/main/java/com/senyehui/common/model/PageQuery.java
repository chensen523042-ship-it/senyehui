package com.senyehui.common.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 统一分页查询参数
 */
@Data
public class PageQuery {

    /**
     * 页码 (从 1 开始)
     */
    @Min(value = 1, message = "页码最小值为 1")
    private int pageNum = 1;

    /**
     * 每页条数
     */
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 100, message = "每页条数最大值为 100")
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向 asc/desc
     */
    private String orderDirection = "desc";
}
