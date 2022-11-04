package com.smartstore.probadores.ui.backend.microservices.task.services;

import com.smartstore.probadores.ui.backend.data.HTTPAnswer;
import com.smartstore.probadores.ui.backend.data.Task;
import com.smartstore.probadores.ui.backend.data.entity.Barcode;
import com.smartstore.probadores.ui.backend.data.entity.Branch;
import com.smartstore.probadores.ui.backend.microservices.task.components.TaskClient;
import com.smartstore.probadores.ui.backend.utils.enums.PriorityEnum;
import com.smartstore.probadores.ui.backend.utils.enums.StateEnum;
import org.springframework.stereotype.Service;

import static com.smartstore.probadores.ui.backend.utils.constants.TaskConstants.*;

@Service
public class TaskService {
    private TaskClient taskClient;

    public TaskService(){
        taskClient = new TaskClient();
    }

    public HTTPAnswer createTaskFromFittingRoom(Integer fittingRoom, Branch branch, Barcode barcode){
        Task task = new Task();
        task.setTitle(String.format(TITLE_FORMAT, barcode.getProductCode().getDescription(), fittingRoom, branch.getDescription()));
        task.setDescription(String.format(DESCRIPTION_FORMAT, barcode.getProductCode().getId(), barcode.getId(), barcode.getSize(), barcode.getColour()));
        task.setCategory(FITTING_ROOM_CATEGORY);
        task.setState(StateEnum.PENDING.getValue());
        task.setPriority(PriorityEnum.HIGH.getValue());
        task.setUserId(null);

        return taskClient.addTask(task);
    }
}
