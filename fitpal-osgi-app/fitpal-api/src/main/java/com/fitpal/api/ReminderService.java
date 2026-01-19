package com.fitpal.api;

import java.util.List;

public interface ReminderService {
    Reminder createReminder(Reminder reminder, String userId);
    List<Reminder> getReminders(String userId);
    List<Reminder> getNotifications(String userId);
    void deleteReminder(String id);
    Reminder updateReadStatus(String id);
    Reminder updateReminder(String id, Reminder reminder);
}