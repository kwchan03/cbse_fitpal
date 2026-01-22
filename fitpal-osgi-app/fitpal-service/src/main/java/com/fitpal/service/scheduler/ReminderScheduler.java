package com.fitpal.service.scheduler;

import com.fitpal.api.Reminder;
import com.fitpal.service.db.ReminderRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component(immediate = true)
public class ReminderScheduler {

    @Reference
    private ReminderRepository reminderRepository;

    private ScheduledExecutorService scheduler;

    @Activate
    public void start() {
        System.out.println(">>> Reminder Scheduler Started");
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(this::checkReminders, 0, 60, TimeUnit.SECONDS);
    }

    @Deactivate
    public void stop() {
        System.out.println(">>> Reminder Scheduler Stopped");
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void checkReminders() {
        try {
            String currentDate = LocalDate.now().toString(); // yyyy-MM-dd
            String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

            List<Reminder> dueReminders = reminderRepository.findByDateAndTimeAndType(currentDate, currentTime, "reminder");

            for (Reminder r : dueReminders) {
                createNotification(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotification(Reminder origin) {
        Reminder note = new Reminder();
        note.setUserId(origin.getUserId());
        note.setTitle("Alert: " + origin.getTitle());
        note.setNotes("Scheduled time reached for: " + origin.getTitle());
        note.setDate(origin.getDate());
        note.setTime(origin.getTime());
        note.setCategory(origin.getCategory());
        note.setType("notification");
        note.setReadStatus(false);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        reminderRepository.save(note);
        System.out.println("Generated notification for user: " + origin.getUserId());
    }
}