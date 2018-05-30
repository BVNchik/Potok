package ru.kodep.potok.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import ru.kodep.potok.PotokApp;
import ru.kodep.potok.R;
import ru.kodep.potok.repository.DataRepository;

public class FragmentLog extends Fragment {
    PotokApp mApp;
    DataRepository mRepository;
    TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_fragment, container, false);
        textView = view.findViewById(R.id.tvLog);
        mApp = (PotokApp) getActivity().getApplication();
        mRepository = mApp.getDataRepository();
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText(mRepository.readFile());
        return view;
    }

}