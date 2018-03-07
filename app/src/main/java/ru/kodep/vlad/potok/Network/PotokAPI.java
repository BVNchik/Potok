package ru.kodep.vlad.potok.Network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.kodep.vlad.potok.models.AuthorizationModel;
import ru.kodep.vlad.potok.models.Credentials;
import ru.kodep.vlad.potok.models.ResponseData;
import ru.kodep.vlad.potok.models.User;
import rx.Single;


/**
 * Created by vlad on 22.02.18
 */
public interface PotokAPI {


    @GET("v1/call/applicants?")
    Single<ResponseData<List<User>>> customerData(
            @Header("Authorization") String bearerToken,
            @Query("by_updated_at_period[from]") String lastRequest
    );

    @POST("sessions")
    Call<AuthorizationModel> req(
            @Body Credentials credentials
    );

}
