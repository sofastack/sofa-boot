package com.alipay.sofa.batch.job;


import lombok.Getter;
import lombok.Setter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author tzeyong
 * Date: 2019-04-17
 * Time: 19:39
 */
@Getter
@Setter
public class JobContext {
    /**
     * name of the job
     */
    private String jobName;

    /**
     * A field to uniquely identify a job request.
     * Task service implementation shall use this to identify if a particular job request has been executed before.
     */
    private String jobRequestId;

    /**
     * Job parameters required by task service for execution of the job
     */
    private JobParameters jobParameters;

    /**
     * Status of the batch job
     */
    private BatchStatus batchStatus = BatchStatus.STARTING;

    /**
     * Data of the job context that will be passed along the processing chain
     */
    private Map<String, Object> data = new ConcurrentHashMap<>();

    /**
     * Put data into this job context
     *
     * @param key
     * @param value
     */
    public void putData(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Get the data from context
     *
     * @param key
     * @return
     */
    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("{jobName:%s, jobRequestId:%s, batchStatus:%s}",
                jobName, jobRequestId, batchStatus));
        if (null != jobParameters) {
            sb.append(String.format("%njobParameters="));
            sb.append(jobParameters.toString());
        }
        sb.append(String.format("%ndata=%s", data.toString()));
        return sb.toString();
    }
}
