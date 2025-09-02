package com.example.demo.controller;

import com.example.demo.entity.Requirement;
import com.example.demo.repository.RequirementRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RequirementController {

    @Autowired
    private RequirementRepository repository;

    // 提交需求
    @PostMapping("/submit")
    public Requirement submit(@RequestBody Requirement req) {
        // 获取当前时间，并转换为上海时区
        ZonedDateTime shanghaiTime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        // 设置提交时间（确保没有时区偏差，保存时减去8小时）
        req.setSubmitTime(shanghaiTime.minusHours(8).toLocalDateTime());

        // 生成 ID 序列
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        long count = repository.count((root, query, cb) -> cb.between(root.get("submitTime"), startOfDay, endOfDay));
        long seq = count + 1;

        String id = today.format(DateTimeFormatter.BASIC_ISO_DATE) + String.format("%04d", seq);
        req.setId(id);

        return repository.save(req);
    }

    // 获取需求记录
    @GetMapping("/records")
    public Page<Requirement> getRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String applyDepartment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedTimeStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedTimeEnd) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "submitTime"));

        Specification<Requirement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (department != null && !department.isEmpty()) {
                predicates.add(cb.like(root.get("department"), "%" + department + "%"));
            }
            if (applyDepartment != null && !applyDepartment.isEmpty()) {
                predicates.add(cb.like(root.get("applyDepartment"), "%" + applyDepartment + "%"));
            }
            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("submitTime"), startTime.atStartOfDay()));
            }
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("submitTime"), endTime.plusDays(1).atStartOfDay()));
            }
            if (expectedTimeStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expectedSupportTime"), expectedTimeStart.atStartOfDay()));
            }
            if (expectedTimeStart != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expectedSupportTime"), expectedTimeEnd.atStartOfDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Requirement> result = repository.findAll(spec, pageable);

        // 日志记录
        System.out.println("Query result: " + result.getContent());

        // 确保返回的数据结构正确，避免空内容导致前端报错
        if (result == null || result.getContent() == null) {
            result = Page.empty(pageable);  // 如果没有数据，返回空 Page
        }

        // 将查询到的所有提交时间字段转换为上海时区
        result.getContent().forEach(req -> {
            if (req.getExpectedSupportTime() != null) {
                req.setExpectedSupportTime(req.getExpectedSupportTime().plusHours(8)); // 转换为上海时区
            }
            if (req.getSubmitTime() != null) {
                req.setSubmitTime(req.getSubmitTime().plusHours(8)); // 转换为上海时区
            }
        });

        return result;
    }

    // 获取部门列表
    @GetMapping("/departments")
    public List<String> getDepartments() {
        return List.of("渭滨区", "金台区", "陈仓区", "凤翔区", "岐山县", "扶风县", "眉县", "陇县", "千阳县", "麟游县", "凤县", "太白县");
    }

    // 获取申请部门列表
    @GetMapping("/applyDepartment")
    public List<String> getApplyDepartment() {
        return List.of("党政要客客户中心", "工业客户中心", "公检法司客户中心", "教育文宣客户中心", "金融农业交通客户中心", "商业客户中心", "住卫健应急客户中心");
    }
}
