package ru.kodep.potok.network;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.kodep.potok.models.AuthorizationModel;
import ru.kodep.potok.models.Credentials;
import ru.kodep.potok.models.ResponseData;
import ru.kodep.potok.models.User;
import rx.Single;

import static ru.kodep.potok.network.ConstAPI.HEADER_AUTHORIZATION;
import static ru.kodep.potok.network.ConstAPI.UPDATED_FROM;


/**
 * Created by vlad on 22.02.18
 */
public interface PotokAPI {

    @GET("call/applicants?")
    Single<ResponseData<List<User>>> customerData(
            @Header(HEADER_AUTHORIZATION) String bearerToken,
            @Query(UPDATED_FROM) String lastRequest);

    @POST("sessions")
    Single<AuthorizationModel> req(
            @Body Credentials credentials );
}

