package ru.kodep.vlad.testingreadnumberphone.Network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ru.kodep.vlad.testingreadnumberphone.models.AuthorizationModel;
import ru.kodep.vlad.testingreadnumberphone.models.Credentials;
import ru.kodep.vlad.testingreadnumberphone.models.ResponseData;
import ru.kodep.vlad.testingreadnumberphone.models.User;
import rx.Single;


/**
 * Created by vlad on 22.02.18
 */
public interface PotokAPI {


    @GET("v1/call/applicants?by_updated_at_period[from]=1970-01-01T12:00:00+03:00")
    Single<ResponseData<List<User>>> customerData(
            @Header("Authorization") String bearerToken
    );

    @POST("sessions")
    Call<AuthorizationModel> req(
            @Body Credentials credentials
    );

}
