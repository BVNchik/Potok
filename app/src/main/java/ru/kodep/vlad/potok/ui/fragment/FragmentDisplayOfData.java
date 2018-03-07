package ru.kodep.vlad.potok.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

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
    TextView tvSynchronization;


    private Subscription mSubscription;

    @SuppressLint("ValidFragment")
    public FragmentDisplayOfData() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                            tvSynchronization.setText("Последняя синхронизация: " + mRepository.getLastRequest());
                    }
                }, new Action1<Throwable>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void call(Throwable throwable) {
                        //обработать исключение
                        tvSynchronization.setText("Исключение: " + throwable);
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
        tvSynchronization = view.findViewById(R.id.tvSynchronization);
        view.findViewById(R.id.btnOut).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOut:
                outAccount();
        }
    }

    private void outAccount() {
        new DataCleaning(getContext());
        FragmentAuthorization fragmentAuthorization = new FragmentAuthorization();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentAuthorization)
                .commit();
    }

}
