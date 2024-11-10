package com.example.sweet_dreams.service.serviceImpl;


import com.example.sweet_dreams.config.PasswordUtils;
import com.example.sweet_dreams.dto.admin.AdminCreateDto;
import com.example.sweet_dreams.dto.admin.AdminDto;
import com.example.sweet_dreams.exception.AdminAlreadyExistsException;
import com.example.sweet_dreams.exception.AdminNotFoundException;
import com.example.sweet_dreams.model.Admin;
import com.example.sweet_dreams.repository.AdminRepository;
import com.example.sweet_dreams.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    @Override
    public void createAdmin(AdminCreateDto adminCreateDto) {
        // Проверяем существование админа с таким username
        if (adminRepository.existsByUsername(adminCreateDto.getUsername())) {
            throw new AdminAlreadyExistsException("Admin with username " + adminCreateDto.getUsername() + " already exists");
        }

        // Создаем нового админа
        Admin admin = new Admin();
        admin.setUsername(adminCreateDto.getUsername());
        admin.setEmail(adminCreateDto.getEmail());
        admin.setRole(adminCreateDto.getRole() != null ? adminCreateDto.getRole() : Admin.AdminRole.MANAGER);

        // Генерируем соль и хешируем пароль
        String salt = PasswordUtils.generateSalt();
        admin.setSalt(salt);
        admin.setPassword(PasswordUtils.hashPassword(adminCreateDto.getPassword(), salt));

        // Устанавливаем остальные поля
        admin.setActive(true);
        admin.setCreatedAt(LocalDateTime.now());

        adminRepository.save(admin);
    }

    public boolean checkPassword(Admin admin, String password) {
        String hashedPassword = PasswordUtils.hashPassword(password, admin.getSalt());
        return hashedPassword.equals(admin.getPassword());
    }

    @Override
    public void updateAdmin(AdminDto adminDto) {
        Admin admin = adminRepository.findById(adminDto.getId())
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with id: " + adminDto.getId()));

        // Проверяем, не пытаемся ли мы обновить username на уже существующий
        if (!admin.getUsername().equals(adminDto.getUsername()) &&
                adminRepository.existsByUsername(adminDto.getUsername())) {
            throw new AdminAlreadyExistsException("Admin with username " + adminDto.getUsername() + " already exists");
        }

        // Обновляем данные
        admin.setUsername(adminDto.getUsername());
        admin.setEmail(adminDto.getEmail());
        admin.setRole(adminDto.getRole());
        admin.setActive(adminDto.isActive());

        adminRepository.save(admin);
    }

    @Override
    public AdminDto findById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new AdminNotFoundException("Admin not found with id: " + id));

        return convertToDto(admin);
    }

    @Override
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new AdminNotFoundException("Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
    }

    @Override
    public List<AdminDto> findAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Вспомогательные методы
    private AdminDto convertToDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .role(admin.getRole())
                .active(admin.isActive())
                .createdAt(admin.getCreatedAt())
                .build();
    }
}
