package com.tatarchenko.task.processor;

import com.tatarchenko.task.model.Activity;
import com.tatarchenko.task.model.Change;
import com.tatarchenko.task.model.History;
import com.tatarchenko.task.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ActivityHistoryProcessor {

    private HistoryRepository historyRepository;

    @Autowired
    public ActivityHistoryProcessor(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void recordActivityHistory(Activity newActivity) {
        recordActivityHistory(null, newActivity);
    }

    public void recordActivityHistory(Activity oldActivity, Activity newActivity) {
        try {
            History history = new History();

            if (oldActivity == null) {
                history.setType(History.Type.COMPOSE);
                history.setChanges(getChanges(new Activity(), newActivity));
            } else {
                history.setType(History.Type.UPDATE);
                history.setChanges(getChanges(oldActivity, newActivity));
            }
            history.setDateTime(LocalDateTime.now());

            historyRepository.save(history);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private List<Change> getChanges(Activity oldActivity, Activity newActivity) throws IllegalAccessException {
        List<Change> changes = new ArrayList<>();

        for (Field field : Activity.class.getDeclaredFields()) {
            field.setAccessible(true);
            String oldValue = Objects.toString(field.get(oldActivity));
            String newValue = Objects.toString(field.get(newActivity));

            Change change = new Change(field.getName(), oldValue, newValue);
            changes.add(change);
        }

        return changes;
    }
}
