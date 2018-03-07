package ru.kodep.vlad.potok.Service;

import android.content.Context;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import ru.kodep.vlad.potok.Network.MyJobService;

/**
 * Created by vlad on 07.03.18
 */

public class JobDispatcher {
public JobDispatcher(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job job = dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(MyJobService.class)
                .setTag("UniqueTagForYourJob")
                .setReplaceCurrent(false)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(10 * 60, 12 * 60))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .build();
        dispatcher.mustSchedule(job);
    }
}
