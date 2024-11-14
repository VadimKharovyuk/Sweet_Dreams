package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.message.ContactMessageDto;
import com.example.sweet_dreams.maper.ContactMessageMapper;
import com.example.sweet_dreams.model.ContactMessage;
import com.example.sweet_dreams.repository.ContactMessageRepository;
import com.example.sweet_dreams.service.serviceImpl.ContactMessageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactMessageServiceImpl implements ContactMessageService {
    private final ContactMessageMapper contactMessageMapper;
    private final ContactMessageRepository contactMessageRepository;

    @Override
    public ContactMessageDto createContactMessage(ContactMessageDto contactMessageDto) {
        log.info("Создание нового сообщения: {}", contactMessageDto);

        try {
            ContactMessage contactMessage = contactMessageMapper.toEntity(contactMessageDto);
            ContactMessage savedMessage = contactMessageRepository.save(contactMessage);
            log.info("Сообщение успешно создано с ID: {}", savedMessage.getId());

            return contactMessageMapper.toDto(savedMessage);
        } catch (Exception e) {
            log.error("Ошибка при создании сообщения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать сообщение", e);
        }
    }

    @Override
    public ContactMessageDto getContactMessageById(Long id) {
        log.info("Получение сообщения по ID: {}", id);

        try {
            ContactMessage contactMessage = contactMessageRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Сообщение не найдено с ID: " + id));

            log.info("Сообщение успешно получено: {}", contactMessage);
            return contactMessageMapper.toDto(contactMessage);
        } catch (EntityNotFoundException e) {
            log.warn("Сообщение не найдено с ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении сообщения с ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось получить сообщение", e);
        }
    }

    @Override
    public void deleteContactMessageById(Long id) {
        log.info("Удаление сообщения по ID: {}", id);

        try {
            if (!contactMessageRepository.existsById(id)) {
                throw new EntityNotFoundException("Сообщение не найдено с ID: " + id);
            }

            contactMessageRepository.deleteById(id);
            log.info("Сообщение успешно удалено с ID: {}", id);
        } catch (EntityNotFoundException e) {
            log.warn("Попытка удаления несуществующего сообщения с ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при удалении сообщения с ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить сообщение", e);
        }
    }

    @Override
    public List<ContactMessageDto> getAllMessages() {
        log.info("Получение всех сообщений");
        try {
            List<ContactMessage> contactMessages = contactMessageRepository.findAll();
            List<ContactMessageDto> dtos = contactMessageMapper.toDtoList(contactMessages);
            log.info("Успешно получено {} сообщений", dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("Ошибка при получении списка сообщений: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось получить список сообщений", e);
        }
    }
}
