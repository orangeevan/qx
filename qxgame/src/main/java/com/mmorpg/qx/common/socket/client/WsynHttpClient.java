package com.mmorpg.qx.common.socket.client;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

import java.io.IOException;


/**
 * http 同步客户端
 * 
 * @author wangke
 * @since v1.0 2017年6月3日
 *
 */
public class WsynHttpClient {
	private static OkHttpClient client = new OkHttpClient();
	private Builder builder;

	public WsynHttpClient(String url) {
		builder = new Request.Builder();
		builder.url(url);
	}

	public Response execute() throws IOException {
		return execute(false);
	}

	public Response execute(boolean print) throws IOException {
		Response response = client.newCall(builder.build()).execute();
		if (!response.isSuccessful()) {
			throw new IOException("服务器端错误: " + response);
		}

		if (print) {
			Headers responseHeaders = response.headers();
			for (int i = 0; i < responseHeaders.size(); i++) {
				System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
			}

			System.out.println(response.body().string());
		}
		return response;
	}

	public Builder getBuilder() {
		return builder;
	}

}
