package kr.co.tbell.echeck.network;

import kr.co.tbell.echeck.model.dto.EcheckApiResult;
import kr.co.tbell.echeck.model.dto.EcheckRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EcheckAPI {

    @POST("ocr")
    Call<EcheckApiResult> getOcrResult(@Body EcheckRequest request);
}
