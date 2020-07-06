package com.tatarchenko.task.controller;


import com.tatarchenko.task.exception.ActivityNotFoundException;
import com.tatarchenko.task.model.Activity;
import com.tatarchenko.task.processor.ActivityHistoryProcessor;
import com.tatarchenko.task.repository.ActivityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private ActivityRepository activityRepository;
    private ActivityHistoryProcessor activityHistoryProcessor;

    @Autowired
    public ActivityController(ActivityRepository activityRepository, ActivityHistoryProcessor activityHistoryProcessor) {
        this.activityRepository = activityRepository;
        this.activityHistoryProcessor = activityHistoryProcessor;
    }

    @PostMapping
     Activity add(@Valid @RequestBody Activity activity) {
        Activity savedActivity = activityRepository.save(activity);
        activityHistoryProcessor.recordActivityHistory(savedActivity);

        return savedActivity;
    }

    @GetMapping
    List<Activity> getAll() {
        return activityRepository.findAll();
    }

    @GetMapping("/{id}")
    Activity get(@PathVariable("id") String id) {
        return activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
    }

    @PutMapping("/{id}")
    Activity replace(@PathVariable("id") String id, @Valid @RequestBody Activity activity) {
        Activity oldActivity = activityRepository.findById(id).orElse(null);
        if(oldActivity != null) {
            activity.setId(oldActivity.getId());
        }
        Activity savedActivity = activityRepository.save(activity);

        activityHistoryProcessor.recordActivityHistory(oldActivity, savedActivity);

        return savedActivity;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
