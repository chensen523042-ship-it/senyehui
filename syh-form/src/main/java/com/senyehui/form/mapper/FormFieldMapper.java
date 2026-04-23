package com.senyehui.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.senyehui.form.entity.FormField;
import org.apache.ibatis.annotations.Mapper;

/**
 * 表单字段数据访问
 */
@Mapper
public interface FormFieldMapper extends BaseMapper<FormField> {
}
