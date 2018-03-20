package ru.kodep.vlad.potok.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.kodep.vlad.potok.DataCleaning;
import ru.kodep.vlad.potok.PotokApp;
import ru.kodep.vlad.potok.R;
import ru.kodep.vlad.potok.repository.DataRepository;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by vlad on 22.02.18
 */

public class FragmentDisplayOfData extends Fragment implements View.OnClickListener {
    TextView tvDescription, tvOnAndOff;
public final  static int PERMISSION_REQUEST_CODE= 1;

    private Subscription mSubscription;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExternalStorageFiles();
        setRetainInstance(true);
        potokApp();
    }

    public void potokApp() {
        PotokApp app = (PotokApp) getActivity().getApplication();
        final DataRepository mRepository = app.getDataRepository();
        mSubscription = mRepository.loadUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Boolean aBoolean) {

                        tvDescription.setText("Последняя синхронизация: " + mRepository.getLastRequest());
                    }
                }, new Action1<Throwable>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Throwable throwable) {
                        //обработать исключение
                        throwable.printStackTrace();
                        tvDescription.setText("Исключение: " + throwable);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @SuppressLint({"ResourceType", "InflateParams"})
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_display_of_data, null);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvOnAndOff = view.findViewById(R.id.tvOnAndOff);
        view.findViewById(R.id.btnOut).setOnClickListener(this);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOut:
                outAccount();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void outAccount() {
        new DataCleaning(getContext());
        FragmentAuthorization fragmentAuthorization = new FragmentAuthorization();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentAuthorization)
                .commit();
    }

public  void noPermission(){
        tvOnAndOff.setText(R.string.off);
        tvOnAndOff.setBackgroundResource(R.drawable.rounded_tv_off);
    tvDescription.setText("Уважаемый пользователь!\nДля работы приложения требуется разрешение на чтение состояние телефона при звонке. Пожалуйста включите разрешение в настройках приложения!");

}
    public void getExternalStorageFiles() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_DENIED) {
            Log.i(getClass().getName(), "запрос на пермишен");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {
                            Manifest.permission.READ_PHONE_STATE,
                    },
                    PERMISSION_REQUEST_CODE);
        }

    }

}
