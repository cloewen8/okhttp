/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package okhttp3.internal.connection

import okhttp3.*
import okhttp3.agragps.CodecFactory
import okhttp3.internal.http.ExchangeCodec
import okio.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class RealConnectionTest {
  private val factory = TestValueFactory()

  private val addressA = factory.newAddress("a")
  private val routeA1 = factory.newRoute(addressA)

  @AfterEach
  fun tearDown() {
    factory.close()
  }

  @Test
  fun usesCustomCodec() {
    val pool = factory.newConnectionPool()
    val c1 = factory.newConnection(pool, routeA1, 50L, factory.taskRunner)
    val codec = object : ExchangeCodec {
      override val carrier: ExchangeCodec.Carrier
        get() = throw UnsupportedOperationException()

      override fun createRequestBody(request: Request, contentLength: Long): Sink {
        throw UnsupportedOperationException()
      }

      override fun writeRequestHeaders(request: Request) {
        throw UnsupportedOperationException()
      }

      override fun flushRequest() {
        throw UnsupportedOperationException()
      }

      override fun finishRequest() {
        throw UnsupportedOperationException()
      }

      override fun readResponseHeaders(expectContinue: Boolean): Response.Builder? {
        throw UnsupportedOperationException()
      }

      override fun reportedContentLength(response: Response): Long {
        throw UnsupportedOperationException()
      }

      override fun openResponseBodySource(response: Response): Source {
        throw UnsupportedOperationException()
      }

      override fun trailers(): Headers {
        throw UnsupportedOperationException()
      }

      override fun cancel() {
        throw UnsupportedOperationException()
      }
    }
    val client = OkHttpClient.Builder()
      .codecFactory(object : CodecFactory {
        override fun provideCodec(
          client: OkHttpClient?,
          carrier: ExchangeCodec.Carrier,
          source: BufferedSource,
          sink: BufferedSink
        ): ExchangeCodec {
          return codec
        }
      })
      .build()
    val call = client.newCall(Request(addressA.url)) as RealCall
    val chain = factory.newChain(call)

    Assertions.assertEquals(codec as ExchangeCodec, c1.newCodec(client, chain))
  }
}
