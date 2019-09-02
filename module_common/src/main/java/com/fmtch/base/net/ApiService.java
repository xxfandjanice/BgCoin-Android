package com.fmtch.base.net;

import com.fmtch.base.net.request.SuperRequest;
import com.fmtch.base.net.response.Response;
import com.fmtch.base.net.response.SuperResponse;
import com.fmtch.base.pojo.response.OssTokenResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

	@POST()
	Observable<Response<SuperResponse>> postApi(@Url() String api, @Body SuperRequest request);

	@POST()
	Observable<Response<List<SuperResponse>>> postListApi(@Url() String api, @Body SuperRequest request);

	@POST()
	Observable<Response<SuperResponse>> postApi(@Url() String api);

	@PATCH()
	Observable<Response<SuperResponse>> patchApi(@Url() String api, @Body SuperRequest request);

	@POST()
	Observable<Response<List<SuperResponse>>> postListApi(@Url() String api);

	@POST()
	Observable<Response<SuperResponse>> getApi(@Url() String api, @QueryMap Map<String, String> map);

	@GET()
	Observable<Response<List<SuperResponse>>> getListApi(@Url() String api, @QueryMap Map<String, String> map);

	@GET()
	Observable<Response<SuperResponse>> getApi(@Url() String api);

	@GET()
	Observable<Response<OssTokenResponse>> getOssToken(@Url() String api);

	@GET()
	Observable<Response<List<SuperResponse>>> getListApi(@Url() String api);

	@DELETE()
	Observable<Response<SuperResponse>> deleteApi(@Url() String api);

	@HTTP(method = "DELETE", hasBody = true)
	Observable<Response<SuperResponse>> deleteApi(@Url() String api, @Body SuperRequest request);

	@PUT()
	Observable<Response<SuperResponse>> putApi(@Url() String api, @Body SuperRequest request);

	@Multipart
	@POST()
	Observable<Response<SuperResponse>> uploadListFile(@Url() String api, @Part List<MultipartBody.Part> partList);

	@Streaming
	@GET
	Call<ResponseBody> downloadFile(@Url String api);

	@Multipart
	@POST()
	Call<Response<SuperResponse>> uploadFileWithProgress(@Url() String api, @Part List<MultipartBody.Part> partList);

	@Streaming
	@GET
	Call<ResponseBody> downloadGetFileWithProgress(@Url String api);


}