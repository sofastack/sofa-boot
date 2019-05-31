package com.alipay.sofa.batch.task;

import com.alipay.sofa.batch.job.ExecutionException;
import com.alipay.sofa.batch.job.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class used when executing list of task.
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 02:17
 */
public class TaskExecutionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutionHelper.class);

    /**
     * Helper method to check invocation of task service implementation
     *
     * @param taskService
     * @param jobContext
     * @return
     */
    public static Boolean checkInvokeTask(TaskService taskService, JobContext jobContext) {
        Boolean invokeSuccess = true;
        // prepare the list of exit status that can continue
        List<ExitStatus> okExitStatus = new ArrayList<>();
        okExitStatus.add(ExitStatus.COMPLETED);
        okExitStatus.add(ExitStatus.NOOP);

        try {
            ExitStatus exitStatus = taskService.execute(jobContext);
            // check if exit status is in the list of OK status to continue
            if (!okExitStatus.contains(exitStatus)) {
                // task exit status is not completed. check if task is a terminator.
                if (taskService.checkTerminateJob(exitStatus)) {
                    // task service implementation indicates that job should be terminated
                    LOGGER.warn("[{}] indicates to terminate job for exit status:{}", taskService.getClass().getName(), exitStatus);
                    // ABANDONED is used for steps that have finished processing, but were not successful
                    jobContext.setBatchStatus(BatchStatus.ABANDONED);
                    invokeSuccess = false;
                }
            }
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
            jobContext.setBatchStatus(BatchStatus.FAILED);
            invokeSuccess = false;
        }
        return invokeSuccess;
    }
}
