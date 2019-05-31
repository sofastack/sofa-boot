package com.alipay.sofa.batch.task;

import com.alipay.sofa.batch.job.ExecutionException;
import com.alipay.sofa.batch.job.JobContext;
import org.springframework.batch.core.ExitStatus;

/**
 * Created by IntelliJ IDEA.
 *
 * @author tzeyong
 * Date: 2019-04-17
 * Time: 23:33
 */
public interface TaskService {
    /**
     * Execute the task implementation based on the job parameters in the job context
     *
     * @param jobContext
     * @return
     * @throws ExecutionException
     */
    ExitStatus execute(JobContext jobContext) throws ExecutionException;

    /**
     * <p>
     * To control the life cycle of the job.
     * This will let the task service implementation indicate if job should be terminated base on the exit status
     * </p>
     *
     * @param exitStatus
     * @return true if the job should be terminated based on the exit status
     */
    Boolean checkTerminateJob(ExitStatus exitStatus);
}
