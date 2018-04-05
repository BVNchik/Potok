package ru.kodep.potok.network;

import android.content.Context;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.kodep.potok.PotokApp;
import ru.kodep.potok.models.AuthorizationModel;
import ru.kodep.potok.models.Credentials;
import ru.kodep.potok.models.ResponseData;
import ru.kodep.potok.models.User;
import ru.kodep.potok.repository.DataRepository;
import rx.Single;
import rx.functions.Func1;

/**
 * Created by vlad on 26.02.18
 */

public class NetworkService {

    private static final int API_VERSION = 1;
    private static final String BASE_URL = "https://app.potok.io/api/apps/v" + API_VERSION + "/";
    private PotokAPI mPotokAPI;

    public NetworkService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mPotokAPI = retrofit.create(PotokAPI.class);
    }

    public Single<List<User>> loadUsers(Context context) {
        PotokApp app = (PotokApp) context.getApplicationContext();
        DataRepository mRepository = app.getDataRepository();
        return mPotokAPI.customerData(new Preferences(context).getToken(),mRepository.getLastRequest() )
                .map(new Func1<ResponseData<List<User>>, List<User>>() {
                    @Override
                    public List<User> call(ResponseData<List<User>> listResponseData) {
                        return listResponseData.getData();
                    }
                });
    }

    public Single<AuthorizationModel>  authorization(String email, String password) {
        Credentials credentials = new Credentials();
        credentials.setEmail(email);
        credentials.setPassword(password);
        return mPotokAPI.req(credentials);
    }
}
