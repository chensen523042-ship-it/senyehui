package com.senyehui.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("sys_tenant_package")
public class TenantPackage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String packageName;
    private Integer maxUsers;
    private Integer maxEvents;
    private Long maxStorage;
    private String features;
    private BigDecimal price;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }
    public Integer getMaxEvents() { return maxEvents; }
    public void setMaxEvents(Integer maxEvents) { this.maxEvents = maxEvents; }
    public Long getMaxStorage() { return maxStorage; }
    public void setMaxStorage(Long maxStorage) { this.maxStorage = maxStorage; }
    public String getFeatures() { return features; }
    public void setFeatures(String features) { this.features = features; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
