package com.example.sweet_dreams.maper;

import com.example.sweet_dreams.dto.message.ContactMessageDto;
import com.example.sweet_dreams.model.ContactMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContactMessageMapper {

    public ContactMessage toEntity(ContactMessageDto dto) {
        if (dto == null) {
            return null;
        }

        return ContactMessage.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .build();
    }

    public ContactMessageDto toDto(ContactMessage entity) {
        if (entity == null) {
            return null;
        }

        return ContactMessageDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .subject(entity.getSubject())
                .message(entity.getMessage())
                .build();
    }

    public List<ContactMessageDto> toDtoList(List<ContactMessage> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


}
