package ru.kodep.potok.network;


import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        final DataRepository mRepository = app.getDataRepository();
        mRepository.fetchAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(new Date(Calendar.getInstance().getTimeInMillis() ));
                        mRepository.writeFile("Синхронизировано: " + data );
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mRepository.writeFile("Ошибка синхронизации: " +   throwable +  throwable.getMessage());
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
