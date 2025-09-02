package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "requirements")
public class Requirement {

    @Id
    private String id;

    private String department;
    private String applyDepartment;
    private String customerName;
    private String businessCode;
    private String requirementName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expectedSupportTime;

    private String contactName;
    private String contactPhone;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime submitTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));

    // ---- Getter / Setter ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getApplyDepartment() { return applyDepartment; }
    public void setApplyDepartment(String applyDepartment) { this.applyDepartment = applyDepartment; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getBusinessCode() { return businessCode; }
    public void setBusinessCode(String businessCode) { this.businessCode = businessCode; }
    public String getRequirementName() { return requirementName; }
    public void setRequirementName(String requirementName) { this.requirementName = requirementName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getExpectedSupportTime() { return expectedSupportTime; }
    public void setExpectedSupportTime(LocalDateTime expectedSupportTime) { this.expectedSupportTime = expectedSupportTime; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getSubmitTime() { return submitTime; }
    public void setSubmitTime(LocalDateTime submitTime) { this.submitTime = submitTime; }
}
