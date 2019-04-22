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
public class SimpleTaskAImpl extends AbstractRepeatableTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTaskAImpl.class);

    @Override
    public Boolean checkTerminateJob(ExitStatus exitStatus) {
        // This task implementation states that job should not terminate regardless of it's exit status
        return false;
    }

    @Override
    public ExitStatus preRepeat(JobContext jobContext) throws ExecutionException {
        LOGGER.debug("doing preRepeat for {}", jobContext.toString());
        jobContext.putData("taskA.preRepeat", UUID.randomUUID().toString());
        return ExitStatus.COMPLETED;
    }

    @Override
    public ExitStatus executeRepeatable(JobContext jobContext) throws ExecutionException {
        LOGGER.debug("doing executeRepeatable for {}", jobContext.toString());
        jobContext.putData("taskA.executeRepeatable", UUID.randomUUID().toString());
        return ExitStatus.COMPLETED;
    }
}
