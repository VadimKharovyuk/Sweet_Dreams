package com.example.sweet_dreams.service;

import com.example.sweet_dreams.dto.message.ContactMessageDto;

import java.util.List;


public interface ContactMessageService {
    ContactMessageDto createContactMessage(ContactMessageDto contactMessageDto);
    ContactMessageDto getContactMessageById(Long id);
    void deleteContactMessageById(Long id);

    List<ContactMessageDto> getAllMessages();

}
