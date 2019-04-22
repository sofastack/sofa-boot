package com.alipay.sofa.batch.task;

import com.alipay.sofa.batch.job.ExecutionException;
import com.alipay.sofa.batch.job.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 *
 * @author tzeyong
 * Date: 2019-04-18
 * Time: 02:34
 */
public class SimpleTaskBImpl extends AbstractNonRepeatableTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTaskBImpl.class);

    @Override
    public Boolean checkTerminateJob(ExitStatus exitStatus) {
        // This task implementation states that job should not terminate regardless of it's exit status
        return false;
    }

    @Override
    public ExitStatus executeNonRepeatable(JobContext jobContext) throws ExecutionException {
        LOGGER.debug("doing executeNonRepeatable for {}", jobContext.toString());
        jobContext.putData("taskB.executeNonRepeatable", UUID.randomUUID().toString());
        return ExitStatus.COMPLETED;
    }

    @Override
    public Boolean checkExecutedBefore(JobContext jobContext) {
        // Do checks if the particular job request has been executed before.
        LOGGER.debug("Checking if executed before for {}", jobContext.toString());
        jobContext.putData("taskB.checkExecutedBefore", UUID.randomUUID().toString());
        // return false for not executed before
        return false;
    }
}
