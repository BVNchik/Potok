package ru.kodep.potok.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.kodep.potok.PotokApp;
import ru.kodep.potok.models.AuthorizationModel;
import ru.kodep.potok.models.Credentials;
import ru.kodep.potok.repository.DataRepository;


/**
 * Created by vlad on 26.02.18
 */

public class AuthorizationRequest {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    private Context context;
    private OnAuthorizationChangedCallback mAuthorizationChangedCallback;

    public AuthorizationRequest(Context context, String email, String password, OnAuthorizationChangedCallback activity) {
        mAuthorizationChangedCallback = activity;
        this.context = context;
        authorization(email, password);
    }

    private void authorization(String email, String password) {
        Credentials credentials = new Credentials();
        credentials.setEmail(email);
        credentials.setPassword(password);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://app.potok.io/api/apps/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PotokAPI potokAPI = retrofit.create(PotokAPI.class);
        final Call<AuthorizationModel> customerDataCall = potokAPI.req(credentials);
        customerDataCall.enqueue(new Callback<AuthorizationModel>() {
            @Override
            public void onResponse(@NonNull Call<AuthorizationModel> call, @NonNull retrofit2.Response<AuthorizationModel> customerDataResponse) {
                if (customerDataResponse.isSuccessful()) {
                    PotokApp app = (PotokApp) context.getApplicationContext();
                    DataRepository mRepository = app.getDataRepository();
                    String token = customerDataResponse.body().getToken();
                    String validTo = customerDataResponse.body().getValidTo();
                    Date dateValidTo;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("RFC"));
                    try {
                        dateValidTo = simpleDateFormat.parse(validTo);
                    } catch (ParseException e) {
                        dateValidTo = new Date();
                    }
                    mRepository.getmPreferences().setToken(token);
                    mRepository.getmPreferences().setValidTo(dateValidTo.getTime());
                    mAuthorizationChangedCallback.whenAuthorizing();
                } else {
                    mAuthorizationChangedCallback.authorizationFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthorizationModel> call, @NonNull Throwable t) {
                //
            }

        });
    }

    public interface OnAuthorizationChangedCallback {
        void whenAuthorizing();

        void authorizationFailed();
    }
}
