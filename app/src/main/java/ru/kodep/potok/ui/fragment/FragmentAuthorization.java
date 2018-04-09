package ru.kodep.potok.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import ru.kodep.potok.PotokApp;
import ru.kodep.potok.R;
import ru.kodep.potok.models.Credentials;
import ru.kodep.potok.repository.DataRepository;
import ru.kodep.potok.utils.Logger;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.widget.Toast.LENGTH_LONG;

public class FragmentAuthorization extends Fragment implements View.OnClickListener {
    public static final String URI = "https://potok.io/";
    EditText etEmailPerson;
    EditText etPassword;
    TextView tvCheckIn;
    TextView tvTitle;
    Button btnComeIn;
    Credentials credentials;
    ProgressBar progressBar;
    FragmentDisplayOfData fragmentDisplayOfData;
    PotokApp mApp;
    DataRepository mRepository;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mApp = (PotokApp) getActivity().getApplication();
        mRepository = mApp.getDataRepository();
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_authorization_screen, container, false);

        view.setOnClickListener(this);
        etEmailPerson = view.findViewById(R.id.etEmailPerson);
        etPassword = view.findViewById(R.id.etPassword);
        btnComeIn = view.findViewById(R.id.btnToComeIn);
        progressBar = view.findViewById(R.id.progressBar2);
        tvCheckIn = view.findViewById(R.id.tvCheckIn);
        tvTitle = view.findViewById(R.id.tvTitle);
        view.findViewById(R.id.tvCheckIn).setOnClickListener(this);
        view.findViewById(R.id.btnToComeIn).setOnClickListener(this);
        credentials = new Credentials();
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    removeTheKeyboard(view);
                    authorization();
                    progressBar.setVisibility(View.VISIBLE);
                    tvTitle.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        if (isOpen) {
                            tvTitle.setVisibility(View.GONE);
                            tvCheckIn.setVisibility(View.GONE);
                        } else {
                            tvCheckIn.setVisibility(View.VISIBLE);
                            if (progressBar.getVisibility() != View.VISIBLE) {
                                tvTitle.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnToComeIn) {
            authorization();
            progressBar.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            removeTheKeyboard(v);
        }
        if (v.getId() == R.id.tvCheckIn) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URI));
            startActivity(intent);
        }
        if (v.getId() == R.id.fragmentLayoutRoot) {

            removeTheKeyboard(v);

        }
    }

    public void removeTheKeyboard(View v) {
        etPassword.clearFocus();
        etEmailPerson.clearFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    private void authorization() {
        final String email = String.valueOf(etEmailPerson.getText());
        String password = String.valueOf(etPassword.getText());
        Single<Boolean> authorization = mRepository.authorization(email, password);
        authorization.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        whenAuthorizing();
                    }

                    @Override
                    public void onError(Throwable error) {
                        authorizationFailed();
                        Logger.print(error);
                    }
                });
    }


    public void whenAuthorizing() {
        mRepository.getmReminderOfValidity().onCreate(getActivity());
        fragmentDisplayOfData = new FragmentDisplayOfData();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentLayout, fragmentDisplayOfData)
                .commit();
    }


    public void authorizationFailed() {
        progressBar.setVisibility(View.INVISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), R.string.wrong_login_or_password, LENGTH_LONG).show();
    }
}
