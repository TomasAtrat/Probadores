package com.smartstore.probadores.ui.backend.microservices.task.components;

import com.smartstore.probadores.ui.backend.data.HTTPAnswer;
import com.smartstore.probadores.ui.backend.data.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.smartstore.probadores.ui.backend.utils.constants.WebServicesConstants.ADD_TASK_SERVICE;

@Component
public class TaskClient {
    private final RestTemplate restTemplate;

    public TaskClient() {
        this.restTemplate = new RestTemplate();
    }

    public HTTPAnswer addTask(Task task) {
        final String url = ADD_TASK_SERVICE;

        System.out.println("url = " + url);

        return restTemplate.postForObject(url, task, HTTPAnswer.class);
    }
}
