package ru.kodep.vlad.testingreadnumberphone.Network;


import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.kodep.vlad.testingreadnumberphone.PotokApp;
import ru.kodep.vlad.testingreadnumberphone.repository.DataRepository;
import ru.kodep.vlad.testingreadnumberphone.ui.fragment.FragmentDisplayOfData;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by vlad on 06.03.18
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private Subscription mSubscription;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.i(getClass().getName(), "WORK");
        PotokApp app = (PotokApp) getApplication();
        DataRepository mRepository = app.getDataRepository();
        mRepository.loadUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Boolean aBoolean) {

                    }
                }, new Action1<Throwable>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
