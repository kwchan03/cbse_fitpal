package com.fitpal.service;

import com.fitpal.api.Preference;
import com.fitpal.api.Reminder;
import com.fitpal.api.ReminderService;
import com.fitpal.service.db.PreferenceRepository;
import com.fitpal.service.db.ReminderRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component(service = ReminderService.class)
public class ReminderServiceImpl implements ReminderService {

    @Reference
    private ReminderRepository reminderRepository;

    @Reference
    private PreferenceRepository preferenceRepository;

    // --- 1. Create Reminder ---
    @Override
    public Reminder createReminder(Reminder reminder, String userId) {
        reminder.setUserId(userId);
        reminder.setType("reminder");
        reminder.setReadStatus(false);

        if (reminder.getCreatedAt() == null) {
            reminder.setCreatedAt(LocalDateTime.now());
        }

        return reminderRepository.save(reminder);
    }

    // --- 2. Get Reminders (List) ---
    @Override
    public List<Reminder> getReminders(String userId) {
        if (userId == null) return Collections.emptyList();

        // type="reminder", sortField="createdAt", asc=false
        return reminderRepository.findByUserIdAndType(userId, "reminder", "createdAt", false);
    }

    // --- 3. Get Notifications (List) ---
    @Override
    public List<Reminder> getNotifications(String userId) {
        if (userId == null) return Collections.emptyList();

        // type="notification", sortField="date", asc=true
        return reminderRepository.findByUserIdAndType(userId, "notification", "date", true);
    }

    // --- 4. Update Read Status ---
    @Override
    public Reminder updateReadStatus(String id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        reminder.setReadStatus(true);
        return reminderRepository.save(reminder);
    }

    // --- 5. Delete ---
    @Override
    public void deleteReminder(String id) {
        if (!reminderRepository.existsById(id)) {
            throw new RuntimeException("Reminder not found");
        }
        reminderRepository.deleteById(id);
    }

    // --- 6. Update Reminder Details ---
    @Override
    public Reminder updateReminder(String id, Reminder inputData) {
        Reminder existing = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        if (inputData.getTitle() != null) existing.setTitle(inputData.getTitle());
        if (inputData.getDate() != null) existing.setDate(inputData.getDate());
        if (inputData.getTime() != null) existing.setTime(inputData.getTime());
        if (inputData.getCategory() != null) existing.setCategory(inputData.getCategory());
        if (inputData.getLeadTime() != null) existing.setLeadTime(inputData.getLeadTime());
        if (inputData.getRecurring() != null) existing.setRecurring(inputData.getRecurring());
        if (inputData.getNotes() != null) existing.setNotes(inputData.getNotes());

        existing.setUpdatedAt(LocalDateTime.now());

        return reminderRepository.save(existing);
    }

    @Override
    public List<Reminder> getRemindersByStatus(String userId, Boolean readStatus) {
        if (userId == null) return Collections.emptyList();
        return reminderRepository.findByUserIdAndTypeAndReadStatus(userId, "reminder", readStatus);
    }

    @Override
    public Preference getPreferences(String userId) {
        Preference pref = preferenceRepository.findByUserId(userId);
        if (pref == null) {
            pref = new Preference();
            pref.setUserId(userId);
            return preferenceRepository.save(pref);
        }
        return pref;
    }

    @Override
    public Preference updatePreferences(String userId, Preference input) {
        Preference existing = getPreferences(userId);

        if (input.getPushEnabled() != null) existing.setPushEnabled(input.getPushEnabled());
        if (input.getEmailEnabled() != null) existing.setEmailEnabled(input.getEmailEnabled());
        if (input.getDoNotDisturb() != null) existing.setDoNotDisturb(input.getDoNotDisturb());

        return preferenceRepository.save(existing);
    }
}