package com.fitpal.fitpalspringbootapp.services;

import com.fitpal.fitpalspringbootapp.dtos.ReminderDto;
import com.fitpal.fitpalspringbootapp.models.Reminder;
import com.fitpal.fitpalspringbootapp.repositories.ReminderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Transactional
    public ReminderDto createReminder(ReminderDto dto, String userId) {
        Reminder reminder = new Reminder();
        BeanUtils.copyProperties(dto, reminder);

        reminder.setUserId(userId);
        reminder.setType("reminder");
        reminder.setReadStatus(false);

        Reminder saved = reminderRepository.save(reminder);
        return convertToDto(saved);
    }

    public List<ReminderDto> getReminders(String userId) {
        List<Reminder> reminders = reminderRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, "reminder");
        return reminders.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<ReminderDto> getNotifications(String userId) {
        List<Reminder> notifications = reminderRepository.findByUserIdAndTypeOrderByDateAscTimeAsc(userId, "notification");
        return notifications.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteReminder(String id) {
        if (!reminderRepository.existsById(id)) {
            throw new RuntimeException("Reminder not found");
        }
        reminderRepository.deleteById(id);
    }

    @Transactional
    public ReminderDto updateReadStatus(String id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        reminder.setReadStatus(true);
        Reminder updated = reminderRepository.save(reminder);
        return convertToDto(updated);
    }

    @Transactional
    public ReminderDto updateReminder(String id, ReminderDto dto) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setTitle(dto.getTitle());
        reminder.setDate(dto.getDate());
        reminder.setTime(dto.getTime());
        reminder.setCategory(dto.getCategory());
        reminder.setLeadTime(dto.getLeadTime());
        reminder.setRecurring(dto.getRecurring());
        reminder.setNotes(dto.getNotes());

        Reminder updated = reminderRepository.save(reminder);
        return convertToDto(updated);
    }

    private ReminderDto convertToDto(Reminder entity) {
        ReminderDto dto = new ReminderDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}