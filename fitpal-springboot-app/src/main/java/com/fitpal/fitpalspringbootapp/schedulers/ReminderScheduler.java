package com.fitpal.fitpalspringbootapp.schedulers;

import com.fitpal.fitpalspringbootapp.models.Reminder;
import com.fitpal.fitpalspringbootapp.repositories.ReminderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired
    private ReminderRepository reminderRepository;

    @Scheduled(cron = "0 * * * * *")
    public void checkScheduledReminders() {
        String currentDate = LocalDate.now().toString();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

        System.out.println("Scanning reminders for: " + currentDate + " " + currentTime);

        List<Reminder> dueReminders = reminderRepository.findByDateAndTimeAndType(currentDate, currentTime, "reminder");

        for (Reminder reminder : dueReminders) {
            triggerNotification(reminder);
        }
    }

    private void triggerNotification(Reminder originReminder) {
        Reminder notification = new Reminder();
        BeanUtils.copyProperties(originReminder, notification, "id", "createdAt", "updatedAt");

        notification.setType("notification");
        notification.setReadStatus(false);
        notification.setTitle("Alert: " + originReminder.getTitle());
        notification.setNotes("Reminder due now: " + originReminder.getNotes());

        reminderRepository.save(notification);
        System.out.println("Generated notification for user: " + originReminder.getUserId());
    }
}