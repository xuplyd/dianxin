package com.example.demo.controller;

import com.example.demo.entity.Requirement;
import com.example.demo.repository.RequirementRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @PostMapping("/submit")
    public Requirement submit(@RequestBody Requirement req) {
        // 生成 ID 序列
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        long count = repository.count((root, query, cb) ->
                cb.between(root.get("submitTime"), startOfDay, endOfDay)
        );
        long seq = count + 1;

        String id = today.format(DateTimeFormatter.BASIC_ISO_DATE) + String.format("%04d", seq);
        req.setId(id);

        // 设置提交时间为上海时区当前时间
        req.setSubmitTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));

        return repository.save(req);
    }

    @GetMapping("/records")
    public Page<Requirement> getRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String applyDepartment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedTime
    ) {
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
            if (expectedTime != null) {
                predicates.add(cb.equal(root.get("expectedSupportTime"), expectedTime.atStartOfDay()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable);
    }

    @GetMapping("/departments")
    public List<String> getDepartments() {
        return List.of("渭滨区", "金台区", "陈仓区", "凤翔区", "岐山县", "扶风县", "眉县", "陇县", "千阳县", "麟游县", "凤县", "太白县");
    }

    @GetMapping("/applyDepartment")
    public List<String> getApplyDepartment() {
        return List.of("党政要客客户中心", "工业客户中心", "公检法司客户中心", "教育文宣客户中心", "金融农业交通客户中心", "商业客户中心", "住卫健应急客户中心");
    }
}
