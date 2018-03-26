package ru.kodep.potok.ui.fragment;

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

import ru.kodep.potok.PotokApp;
import ru.kodep.potok.R;
import ru.kodep.potok.models.Credentials;
import ru.kodep.potok.network.AuthorizationRequest;
import ru.kodep.potok.repository.DataRepository;

import static android.widget.Toast.LENGTH_LONG;

public class FragmentAuthorization extends Fragment implements View.OnClickListener, AuthorizationRequest.OnAuthorizationChangedCallback {
    public static final String URI = "https://potok.io/";
    EditText etEmailPerson;
    EditText etPassword;
    TextView tvCheckIn;
    TextView tvTitle;
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
        View view = inflater.inflate(R.layout.activity_authorization_screen, container, false);
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
        if (v.getId() == R.id.btnToComeIn) {
            authorization();
            progressBar.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.INVISIBLE);
        }
        if (v.getId() == R.id.tvCheckIn) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI));
            startActivity(intent);
        }
    }

    private void authorization() {
        String email = String.valueOf(etEmailPerson.getText());
        String password = String.valueOf(etPassword.getText());
        authorizationRequest = new AuthorizationRequest(getActivity(), email, password, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void whenAuthorizing() {
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
