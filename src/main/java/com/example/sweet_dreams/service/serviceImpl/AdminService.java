package com.example.sweet_dreams.service.serviceImpl;

import com.example.sweet_dreams.dto.admin.AdminCreateDto;
import com.example.sweet_dreams.dto.admin.AdminDto;
import com.example.sweet_dreams.model.Admin;

import java.util.List;

public interface AdminService {
    void createAdmin(AdminCreateDto adminCreateDto);
    void updateAdmin(AdminDto adminDto);
    AdminDto findById(Long id);
    void deleteAdmin(Long id);
    List<AdminDto> findAllAdmins();

    boolean checkPassword(Admin admin, String password);

}
