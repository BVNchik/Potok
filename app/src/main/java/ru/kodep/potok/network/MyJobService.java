package ru.kodep.potok.network;


import android.os.Build;
import android.support.annotation.RequiresApi;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import ru.kodep.potok.PotokApp;
import ru.kodep.potok.repository.DataRepository;
import ru.kodep.potok.utils.Logger;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by vlad on 06.03.18
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {
        PotokApp app = (PotokApp) getApplication();
        DataRepository mRepository = app.getDataRepository();
        mRepository.loadUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        //
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.print(throwable);
                    }
                });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
