## SOFABoot Scheduler with Batch Sample Project

This project gives a simple demonstration on a sample implementation of a batch framework with SOFABoot.
The modules in this sample project as as follows

```text
.
│
├── batch-job-facade
│
├── batch-task-facade 
│ 
├── batch-task-list-job-provider
│ 
├── sofa-boot-rpc-batch-service
│ 
└── sofa-boot-scheduler
```

Module description：

- batch-job-facade: API Classes used for facilitating job related interfaces for batch processing；
- batch-task-facade: API Classes used for facilitating task related interfaces for batch processing；
- batch-task-list-job-provider: Sample implementation of batch job base on a list of task in a job. 
Utilising batch-job-facade and batch-task-facade；
- sofa-boot-rpc-batch-service: SOFAboot server to provide the implemented batch service;
- sofa-boot-scheduler: SOFAboot application configured to enable scheduler.

## Definition of Service API

### batch-job-facade module
#### JobService class 
it defines the interface that all batch job implementation should adhere to :

```java
public interface JobService {
    JobContext launchJob(JobContext jobContext);
}
```

### batch-task-facade module
#### TaskService class
It defines the interface that all task shall implement.
```java
public interface TaskService {
    ExitStatus execute(JobContext jobContext) throws ExecutionException;
    Boolean checkTerminateJob(ExitStatus exitStatus);
}
```
##### checkTerminateJob(ExitStatus exitStatus) method
It allows a task to signal that it wants to terminate the batch job.

#### AbstractRepeatableTask class
It provides abstract methods that can be used to support task that allows repeated execution.
```java
public abstract class AbstractRepeatableTask implements TaskService {
    public abstract ExitStatus preRepeat(JobContext jobContext) throws ExecutionException;
    public abstract ExitStatus executeRepeatable(JobContext jobContext) throws ExecutionException;
    // ...
}
```

##### preRepeat(JobContext jobContext) method
This method should implement any codes that is required to clean up previous execution (if required).

##### executeRepeatable(JobContext jobContext) method
The implementation of this method shall contains the processing logic that supports repeated execution for the same parameters in the job context.

#### AbstractNonRepeatableTask class
It provides abstract methods that can be used to support 
task that does not allow repeated execution of the same job parameters.
```java
public abstract class AbstractNonRepeatableTask implements TaskService {
    public abstract ExitStatus executeNonRepeatable(JobContext jobContext) throws ExecutionException;
    public abstract Boolean checkExecutedBefore(JobContext jobContext);
    // ...
}
```
##### checkExecutedBefore(JobContext jobContext) method
Implement the checking that is required for the task to determine if it is a repeated execution.

##### executeNonRepeatable(JobContext jobContext) method
Implementation of the logic which should not have repeated execution for the same job parameters.

## Implementation of batch job based on a list of tasks

### batch-task-list-job-provider module
This module provide for functionality of jobs that are executed based on a list of tasks.

#### TaskListJobImpl class
This class implement `JobService`. The behavior is such that when the job is launched, 
a list of task will be executed. The task list can be easily configured in the module xml file.

The xml file can be found as per illustrated in the directory structure below.
`batch-task-list-job-provider > src > main > resources > META-INF > spring > batch-task-list-job-provider.xml`

```xml
    <bean id="simpleTaskAImpl" class="com.alipay.sofa.batch.task.SimpleTaskAImpl"/>
    <bean id="simpleTaskBImpl" class="com.alipay.sofa.batch.task.SimpleTaskBImpl"/>

    <util:list id="sampleJobTaskList" value-type="com.alipay.sofa.batch.task.TaskService">
        <ref bean="simpleTaskAImpl"/>
        <ref bean="simpleTaskBImpl"/>
    </util:list>
    
    <bean id="sampleJobService" class="com.alipay.sofa.batch.job.TaskListJobImpl">
        <property name="taskServiceList" ref="sampleJobTaskList"/>
    </bean>
```

#### Identify as SOFA module
Define `sofa-module.properties` to indicate it is a SOFA module:

`batch-task-list-job-provider > src > main > resources > META-INF > sofa-module.properties`

```properties

Module-Name=com.alipay.sofa.batch.sample.batch-task-list-job-provider
```
## SOFABoot server provides RPC batch service [sofa-boot-rpc-batch-service]
This module provides an example on how to deploy a RPC batch service. 
Multiple instances of this server can be deployed to have redundancy on the batch service.

![Batch HA](resource/sofa-rpc-batch-service-HA.png)

In the above diagram, 

* an example `Batch Service Server` block will be the module `sofa-boot-rpc-batch-service` described here.
* an example `Scheduler` block will be the module `sofa-boot-scheduler` described in subsequent section below. 
  
### sofa-boot-rpc-batch-service
It is easy to configure a SOFA boot server for batch service.
1. In `build.gradle`, add in dependencies for `batch-task-list-job-provider`
2. Use custom port for bolt binding if required. Add the following line in `application.properties`


```properties

# SOFA RPC config (default port for bolt binding is 12200)
com.alipay.sofa.rpc.bolt.port=12121

```

#### Running the SOFA boot server
##### (A) Use gradle task "bootRun"
```text
$ cd sofa-boot-rpc-batch-service
$ gradle bootRun
```
##### (B) Build executable jar
```text
$ cd sofa-boot-rpc-batch-service
$ gradle bootJar
$ java -jar build/libs/sofa-boot-rpc-batch-service-0.0.1-SNAPSHOT.jar 
```

## SOFABoot scheduler sample [sofa-boot-scheduler]
SOFA boot module that illustrate how to work with schedulers invoking jobs via SOFARPC.

### ApplicationRun class
```java
@ComponentScan({"com.alipay.sofa.batch", "com.alipay.sofa.scheduler.sample"})
@SpringBootApplication
@EnableScheduling
@ImportResource({"classpath*:rpc-sofa-boot-scheduler.xml"})
public class ApplicationRun {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApplicationRun.class);
        springApplication.run(args);
    }
}
```
* `@EnableScheduling` enables scheduler in the SOFAboot application.
* `@ImportResource({ "classpath*:rpc-sofa-boot-scheduler.xml" })` loads the xml file required to configure SOFA.

### ScheduledTask
```java
@Component
public class ScheduledTask {
    
    @Autowired()
    @Qualifier("sampleJobService")
    private JobService jobService;

    @Scheduled(cron = "${com.alipay.sofa.scheduler.sample.ScheduledTask.cron:0 * * * * *}")
    public void executeTask() {
        // ...
        jobContext = jobService.launchJob(jobContext);
        // ...
    }   
}
```

For the schedule configuration, default is to trigger every minute.

`@Scheduled(cron = "${com.alipay.sofa.scheduler.sample.ScheduledTask.cron:0 * * * * *}")`, 
it can be overwritten in `application.properties`

```properties
# Cron schedule customisation (if required)
# trigger every 5 seconds
com.alipay.sofa.scheduler.sample.ScheduledTask.cron=*/5 * * * * *

```
### Configure SOFARPC reference
Configuration are done in `rpc-sofa-boot-scheduler.xml`

```xml
    <!-- set jvm-first = "false" because in SOFA, jvm services will have priority over RPC -->
    <sofa:reference id="sampleJobService" interface="com.alipay.sofa.batch.job.JobService" jvm-first="false">
        <sofa:binding.bolt>
            <sofa:global-attrs timeout="3000" address-wait-time="2000"/>
            <sofa:route target-url="127.0.0.1:12121"/>
        </sofa:binding.bolt>
    </sofa:reference>
```

The configuration `<sofa:route target-url="127.0.0.1:12121"/>`, can be pointed to a load balancer in-front of multiple SOFAboot server providing the batch service.

### Running Sample SOFABoot Scheduler
#### Using gradle task bootRun
```text
$ cd sofa-boot-scheduler
$ gradle bootRun
```

#### Build and use executable Jar
```text
$ cd sofa-boot-scheduler
$ gradle bootJar
$ java -jar build/libs/sofa-boot-scheduler-0.0.1-SNAPSHOT.jar
```

#### Sample output at Scheduler (client for SOFARPC)
```text

 ,---.    ,-----.  ,------.   ,---.     ,-----.                     ,--.
'   .-'  '  .-.  ' |  .---'  /  O  \    |  |) /_   ,---.   ,---.  ,-'  '-.
`.  `-.  |  | |  | |  `--,  |  .-.  |   |  .-.  \ | .-. | | .-. | '-.  .-'
.-'    | '  '-'  ' |  |`    |  | |  |   |  '--' / ' '-' ' ' '-' '   |  |
`-----'   `-----'  `--'     `--' `--'   `------'   `---'   `---'    `--'


Spring Boot Version: 2.1.4.RELEASE (v2.1.4.RELEASE)
SOFABoot Version: 3.1.3 (v3.1.3)
Powered By Ant Financial Services Group

...

2019-04-19 17:31:39.004  INFO 45874 --- [   scheduling-1] c.a.sofa.scheduler.sample.ScheduledTask  : JobContext Start : {jobName:sample job name, jobRequestId:1fd8d13a9bdb8acc11e96285eeeaabd4, batchStatus:STARTING}
jobParameters={time-now=1555666299004}
data={}
2019-04-19 17:31:39.008  INFO 45874 --- [   scheduling-1] c.a.sofa.scheduler.sample.ScheduledTask  : JobContext End = {jobName:sample job name, jobRequestId:1fd8d13a9bdb8acc11e96285eeeaabd4, batchStatus:COMPLETED}
jobParameters={time-now=1555666299004}
data={taskB.checkExecutedBefore=260faa9c-9783-4b56-b88f-c10bba7ced65, taskB.executeNonRepeatable=34306403-0950-4c04-b54f-ca865f91fd95, taskA.executeRepeatable=c184ec56-ee23-45a8-a3a3-a3894902f5e7, taskA.preRepeat=a2a0f948-88e5-45e9-a454-36e9e18fe080}
```

#### Sample output at Batch Service Server (service provider for SOFARPC)
```text
2019-04-19 17:31:39.006 DEBUG 45681 --- [-BIZ-12121-3-T4] c.a.sofa.batch.task.SimpleTaskAImpl      : doing preRepeat for {jobName:sample job name, jobRequestId:1fd8d13a9bdb8acc11e96285eeeaabd4, batchStatus:STARTED}
jobParameters={time-now=1555666299004}
data={}
2019-04-19 17:31:39.006 DEBUG 45681 --- [-BIZ-12121-3-T4] c.a.sofa.batch.task.SimpleTaskAImpl      : doing executeRepeatable for {jobName:sample job name, jobRequestId:1fd8d13a9bdb8acc11e96285eeeaabd4, batchStatus:STARTED}
jobParameters={time-now=1555666299004}
data={taskA.preRepeat=a2a0f948-88e5-45e9-a454-36e9e18fe080}
2019-04-19 17:31:39.006 DEBUG 45681 --- [-BIZ-12121-3-T4] c.a.sofa.batch.task.SimpleTaskBImpl      : Checking if executed before for {jobName:sample job name, jobRequestId:1fd8d13a9bdb8acc11e96285eeeaabd4, batchStatus:STARTED}
jobParameters={time-now=1555666299004}
data={taskA.executeRepeatable=c184ec56-ee23-45a8-a3a3-a3894902f5e7, taskA.preRepeat=a2a0f948-88e5-45e9-a454-36e9e18fe080}
2019-04-19 17:31:39.006 DEBUG 45681 --- [-BIZ-12121-3-T4] c.a.sofa.batch.task.SimpleTaskBImpl      : doing executeNonRepeatable for {jobName:sample job name, jobRequestId:1fd8d13a9bdb8acc11e96285eeeaabd4, batchStatus:STARTED}
jobParameters={time-now=1555666299004}
data={taskB.checkExecutedBefore=260faa9c-9783-4b56-b88f-c10bba7ced65, taskA.executeRepeatable=c184ec56-ee23-45a8-a3a3-a3894902f5e7, taskA.preRepeat=a2a0f948-88e5-45e9-a454-36e9e18fe080}
2019-04-19 17:31:39.006  INFO 45681 --- [-BIZ-12121-3-T4] c.alipay.sofa.batch.job.TaskListJobImpl  : Job sample job name requestId:1fd8d13a9bdb8acc11e96285eeeaabd4 COMPLETED
```

You will notice the same job request id: 1fd8d13a9bdb8acc11e96285eeeaabd4, was 
1. initiated at the scheduler client,
2. sent via RPC to SOFA boot batch server, 
3. processed at the batch server and finally
4. response back to the scheduler RPC client.