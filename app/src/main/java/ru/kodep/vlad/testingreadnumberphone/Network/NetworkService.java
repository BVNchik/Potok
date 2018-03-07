package ru.kodep.vlad.testingreadnumberphone.Network;




import android.content.Context;
import android.preference.Preference;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.kodep.vlad.testingreadnumberphone.models.ResponseData;
import ru.kodep.vlad.testingreadnumberphone.models.User;
import rx.Single;
import rx.functions.Func1;

/**
 * Created by vlad on 26.02.18
 */

public class NetworkService {
    private static final String BASE_URL = "https://app.potok.io/api/apps/";
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

        return mPotokAPI.customerData(new Preferences(context).getToken())
                .map(new Func1<ResponseData<List<User>>, List<User>>() {
                    @Override
                    public List<User> call(ResponseData<List<User>> listResponseData) {
                        return listResponseData.getData();
                    }
                });
    }
}
