package ru.kodep.vlad.potok.Network;

import android.content.Context;
import android.support.annotation.NonNull;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import ru.kodep.vlad.potok.models.AuthorizationModel;
import ru.kodep.vlad.potok.models.Credentials;


/**
 * Created by vlad on 26.02.18
 */

public class AuthorizationRequest {
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
                    String token = customerDataResponse.body().getToken();
                    String validTo = customerDataResponse.body().getValidTo();
                    new Preferences(context).setToken(token);
                    new Preferences(context).setValidTo(validTo);
                    mAuthorizationChangedCallback.WhenAuthorizing();

                } else {
                    mAuthorizationChangedCallback.authorizationFailed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthorizationModel> call, @NonNull Throwable t) {
            }

        });
    }

    public interface OnAuthorizationChangedCallback {
        void WhenAuthorizing();

        void authorizationFailed();
    }
}
