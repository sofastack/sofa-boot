package com.alipay.sofa.batch.job;

import com.alipay.sofa.batch.task.TaskExecutionHelper;
import com.alipay.sofa.batch.task.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;

import java.util.List;

/**
 * Class for job service implementation that utilise a list of task service.
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 01:40
 */
public class TaskListJobImpl implements JobService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskListJobImpl.class);
    /**
     * The list of task service that will be executed when job is launched to handle a request
     */
    private List<TaskService> taskServiceList;

    public List<TaskService> getTaskServiceList() {
        return taskServiceList;
    }

    public void setTaskServiceList(List<TaskService> taskServiceList) {
        this.taskServiceList = taskServiceList;
    }

    @Override
    public JobContext launchJob(JobContext jobContext) {
        // set BatchStatus as started
        jobContext.setBatchStatus(BatchStatus.STARTED);

        // nothing to do if task list is empty or null
        if (null == taskServiceList || taskServiceList.isEmpty()) {
            LOGGER.warn("No task in list");
            jobContext.setBatchStatus(BatchStatus.COMPLETED);
            return jobContext;
        }

        // invoke each task in the list to complete the job
        Boolean allTaskExecutionSuccess = true;
        for (TaskService taskService : taskServiceList) {
            if (!TaskExecutionHelper.checkInvokeTask(taskService, jobContext)) {
                LOGGER.warn("Invocation of [{}] terminates job {} for requestId:{}"
                        , taskService.getClass().getName(), jobContext.getJobName(), jobContext.getJobRequestId());
                allTaskExecutionSuccess = false;
                break;
            }
        }

        if (allTaskExecutionSuccess) {
            LOGGER.info("Job {} requestId:{} COMPLETED", jobContext.getJobName(), jobContext.getJobRequestId());
            jobContext.setBatchStatus(BatchStatus.COMPLETED);
        }
        return jobContext;
    }
}
