package com.bedfordshire.recipenest.controller;

import com.bedfordshire.recipenest.dto.admin.AdminDashboardResponse;
import com.bedfordshire.recipenest.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    // Service that builds the admin dashboard data
    private final AdminService adminService;


    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard(){
        // Returns real admin dashboard statistics
        return ResponseEntity.ok(adminService.getDashboard());
    }
}
