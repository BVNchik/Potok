package ru.kodep.vlad.potok.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ru.kodep.vlad.potok.PotokApp;
import ru.kodep.vlad.potok.R;
import ru.kodep.vlad.potok.models.Credentials;
import ru.kodep.vlad.potok.network.AuthorizationRequest;
import ru.kodep.vlad.potok.repository.DataRepository;

import static android.widget.Toast.LENGTH_LONG;

public class FragmentAuthorization extends Fragment implements View.OnClickListener, AuthorizationRequest.OnAuthorizationChangedCallback {
    EditText etEmailPerson, etPassword;
    TextView tvCheckIn, tvTitle;
    Button btnComeIn;
    Credentials credentials;
    ProgressBar progressBar;
    FragmentDisplayOfData fragmentDisplayOfData;
    AuthorizationRequest authorizationRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_authorization_screen, null);
        etEmailPerson = view.findViewById(R.id.etEmailPerson);
        etPassword = view.findViewById(R.id.etPassword);
        btnComeIn = view.findViewById(R.id.btnToComeIn);
        progressBar = view.findViewById(R.id.progressBar2);
        tvCheckIn = view.findViewById(R.id.tvCheckIn);
        tvTitle = view.findViewById(R.id.tvTitle);
        view.findViewById(R.id.tvCheckIn).setOnClickListener(this);
        view.findViewById(R.id.btnToComeIn).setOnClickListener(this);
        credentials = new Credentials();
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnToComeIn:
                authorization();
                progressBar.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.INVISIBLE);
                break;
            case R.id.tvCheckIn:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://potok.io/"));
                startActivity(intent);
                break;
        }
    }


    private void authorization() {
        String email = String.valueOf(etEmailPerson.getText());
        String password = String.valueOf(etPassword.getText());
        authorizationRequest = new AuthorizationRequest(getActivity(), email, password, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void WhenAuthorizing() {
        PotokApp app = (PotokApp) getActivity().getApplication();
        DataRepository mRepository = app.getDataRepository();
        mRepository.getmReminderOfValidity().onCreate(getActivity());
        fragmentDisplayOfData = new FragmentDisplayOfData();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentDisplayOfData)
                .commit();
    }

    @Override
    public void authorizationFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), R.string.wrong_login_or_password, LENGTH_LONG).show();
    }
}
