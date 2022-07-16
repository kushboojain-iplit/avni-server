package org.avni.web.api;

import org.avni.dao.ConceptRepository;
import org.avni.dao.IndividualRepository;
import org.avni.dao.UserRepository;
import org.avni.dao.task.TaskRepository;
import org.avni.dao.task.TaskStatusRepository;
import org.avni.dao.task.TaskTypeRepository;
import org.avni.domain.Individual;
import org.avni.domain.task.Task;
import org.avni.domain.task.TaskType;
import org.avni.service.ConceptService;
import org.avni.web.request.api.ApiTaskRequest;
import org.avni.web.request.api.RequestUtils;
import org.avni.web.response.Response;
import org.avni.web.response.api.ApiTaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.UUID;

import static org.avni.web.api.CommonFieldNames.*;
import static org.avni.web.contract.TaskFieldNames.*;

@RestController
public class TaskApiController {
    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final ConceptRepository conceptRepository;
    private final IndividualRepository individualRepository;
    private final ConceptService conceptService;

    @Autowired
    public TaskApiController(TaskRepository taskRepository, TaskTypeRepository taskTypeRepository, UserRepository userRepository, TaskStatusRepository taskStatusRepository, ConceptRepository conceptRepository, IndividualRepository individualRepository, ConceptService conceptService) {
        this.taskRepository = taskRepository;
        this.taskTypeRepository = taskTypeRepository;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.conceptRepository = conceptRepository;
        this.individualRepository = individualRepository;
        this.conceptService = conceptService;
    }

    @PostMapping(value = "/api/task")
    @PreAuthorize(value = "hasAnyAuthority('user')")
    @Transactional
    @ResponseBody
    public ResponseEntity post(@RequestBody ApiTaskRequest request) {
        Task task = new Task();
        TaskType taskType = taskTypeRepository.findByName(request.getTaskTypeName());
        task.setTaskType(taskType);
        task.setAssignedTo(userRepository.findByUsername(request.getAssignedTo()));
        task.setCompletedOn(request.getCompletedOn());
        task.setScheduledOn(request.getScheduledOn());
        task.setLegacyId(request.getExternalId());
        task.setTaskStatus(taskStatusRepository.findByName(request.getTaskStatus()));
        task.setMetadata(RequestUtils.createObservations(request.getMetadata(), conceptRepository));
        task.setObservations(RequestUtils.createObservations(request.getObservations(), conceptRepository));
        task.setName(request.getName());
        Individual individual = individualRepository.getSubject(request.getSubjectId(), request.getSubjectExternalId());
        ApiErrorUtil.throwIfSubjectNotFound(individual, request.getSubjectId(), request.getSubjectExternalId());
        task.setSubject(individual);
        task.setLegacyId(request.getExternalId());
        task.assignUUID();
        task.setVoided(request.isVoided());
        taskRepository.save(task);

        ApiTaskResponse response = new ApiTaskResponse();
        response.put(TASK_TYPE, task.getTaskType().getName());
        response.put(ASSIGNED_TO, task.getAssignedTo().getUsername());
        response.put(COMPLETED_ON, task.getCompletedOn());
        response.put(SCHEDULED_ON, task.getScheduledOn());
        response.put(EXTERNAL_ID, task.getLegacyId());
        response.put(TASK_STATUS, task.getTaskStatus().getName());
        Response.putObservations(conceptRepository, conceptService, response, new LinkedHashMap<>(), task.getMetadata(), METADATA);
        Response.putObservations(conceptRepository, conceptService, response, new LinkedHashMap<>(), task.getObservations(), OBSERVATIONS);
        response.put(NAME, task.getName());
        response.put(SUBJECT_ID, task.getSubject().getUuid());
        response.put(SUBJECT_EXTERNAL_ID, task.getSubject().getLegacyId());
        response.put(ID, task.getUuid());
        response.put(VOIDED, task.isVoided());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}