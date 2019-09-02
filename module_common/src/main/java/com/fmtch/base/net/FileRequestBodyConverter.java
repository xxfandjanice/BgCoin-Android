package com.fmtch.base.net;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

public class FileRequestBodyConverter
		implements Converter<File, RequestBody> {

	@Override
	public RequestBody convert(File file)
			throws IOException {

		return RequestBody.create(
				//				MediaType.parse("application/otcet-stream"),
				MediaType.parse("multipart/form-data"),
				file
								 );
	}
}
