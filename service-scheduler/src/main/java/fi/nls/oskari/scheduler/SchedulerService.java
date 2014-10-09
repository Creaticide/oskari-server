package fi.nls.oskari.scheduler;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * A service for scheduling timed or regular method calls, to be executed in their own threads.
 *
 * The Quartz configuration properties can be set in the scheduler-test.properties file.
 * http://quartz-scheduler.org/documentation/quartz-2.2.x/configuration/
 */
public class SchedulerService {

    private static final Logger log = LogFactory.getLogger(SchedulerService.class);

    private static final String JOBS_KEY = "oskari.scheduler.jobs";

    private Scheduler scheduler;

    public void initializeScheduler() throws SchedulerException {
        final Properties schedulerProperties = PropertyUtil.getProperties();
        final StdSchedulerFactory sf = new StdSchedulerFactory();
        sf.initialize(schedulerProperties);
        scheduler = sf.getScheduler();
        scheduler.start();
        this.setupJobs();
    }

    public void shutdownScheduler() throws SchedulerException {
        scheduler.shutdown(false);
    }

    public void setupJobs() {
        for (final String jobCode : PropertyUtil.getCommaSeparatedList(JOBS_KEY)) {
            final String key = String.format("oskari.scheduler.job.%s", jobCode);
            final String cronLine = PropertyUtil.get(key + ".cronLine");
            final String className = PropertyUtil.get(key + ".className");
            final String methodName = PropertyUtil.get(key + ".methodName");
            this.scheduleMethodCall(jobCode, cronLine, className, methodName);
        }
    }

    public void scheduleMethodCall(final String jobCode, final String cronLine,
                                   final String className, final String methodName)
    {
        final JobDetail job = newJob()
                .ofType(ArbitraryStaticMethodCallJob.class)
                .build();
        final Trigger trigger = newTrigger()
                .withSchedule(cronSchedule(cronLine))
                .usingJobData("className", className)
                .usingJobData("methodName", methodName)
                .build();
        try {
            scheduler.scheduleJob(job, trigger);
            log.info("scheduled job", jobCode);
        } catch (final SchedulerException e) {
            log.error(e, "failed to schedule job", jobCode);
        }
    }

}
