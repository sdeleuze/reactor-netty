/*
 * Copyright (c) 2011-2016 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor.ipc.netty.http;

import java.io.InputStream;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientException;

/**
 * @author Stephane Maldini
 * @since 2.5
 */
public class HttpClientTests {

	//@Test
	public void simpleTest() throws Exception {
		int res = HttpClient.create()
		                    .get("http://next.projectreactor.io/assets/css/reactor.css",
				                    c -> c.followRedirect()
				                          .sendHeaders())
		                    .then(r -> Mono.just(r.status()
		                                          .code()))
		                    .log()
		                    .block();
		res = HttpClient.create()
		                .get("http://next.projectreactor.io/assets/css/reactor.css",
				                c -> c.followRedirect()
				                      .sendHeaders())
		                .then(r -> Mono.just(r.status()
		                                      .code()))
		                .log()
		                .block();

		if (res != 200) {
			throw new IllegalStateException("test status failed with " + res);
		}
	}

	//@Test
	public void postUpload() throws Exception {
		InputStream f = getClass().getResourceAsStream("/public/index.html");
		//Path f = Paths.get("/Users/smaldini/Downloads/IMG_6702.mp4");
		int res = HttpClient.create("google.com")
		                    .put("/post",
				                    c -> c.sendMultipart(form -> form.file("test", f)
				                                                     .attr("att1",
						                                                         "attr2")
				                                                     .file("test2",
						                                                         f))
				                          .log()
				                          .then())
		                    .then(r -> Mono.just(r.status()
		                                          .code()))
		                    .block();
		res = HttpClient.create("google.com")
		                .get("/search",
				                c -> c.followRedirect()
				                      .sendHeaders())
		                .then(r -> Mono.just(r.status()
		                                      .code()))
		                .log()
		                .block();

		if (res != 200) {
			throw new IllegalStateException("test status failed with " + res);
		}
	}

	@Test
	public void simpleTest404() {
		int res = HttpClient.create("google.com")
		                    .get("/unsupportedURI",
				                    c -> c.followRedirect()
				                          .sendHeaders())
		                    .then(r -> Mono.just(r.status()
		                                          .code()))
		                    .log()
		                    .otherwise(HttpClientException.class,
				                    e -> Mono.just(e.getResponseStatus()
				                                    .code()))
		                    .block();

		if (res != 404) {
			throw new IllegalStateException("test status failed with " + res);
		}
	}

}
