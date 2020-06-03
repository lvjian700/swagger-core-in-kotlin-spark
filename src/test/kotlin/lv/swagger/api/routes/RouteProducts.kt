package lv.swagger.api.routes

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.toJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import spark.Spark
import spark.Spark.port
import spark.Spark.stop

object RouteProducts: Spek({
  describe("Products") {
    val port = 8088
    val url = "http://localhost:$port/test/products"

    beforeGroup {
      port(port)
      Spark.path("/test") {
        routeProducts()
      }
    }

    afterGroup {
      stop()
    }

    context("when post product") {
      val requestBody = """{
                  "name": "Kotlin",
                  "quote": 100.00,
                  "startAt": "2020-06-01",
                  "endDate": "2020-07-01"
                }"""

      val expected = JsonObject().apply {
        addProperty("name", "Kotlin")
        addProperty("quote", 100.00)
        addProperty("startAt", "2020-06-01")
        addProperty("endDate", "2020-07-01")
      }

      it("creates product") {

        val response = post(url, requestBody)

        assertThat(response.code, equalTo(201))

        val jsonBody = Gson().fromJson<JsonObject>(response.body?.string()!!)
        assertThat(jsonBody, equalTo(expected))
      }
    }

    context("when missing name on request") {
      val requestBody = """{
                  "quote": 100.00,
                  "startAt": "2020-06-01",
                  "endDate": "2020-07-01"
                }"""

      it("returns bad request") {
        val response = post(url, requestBody)
        assertThat(response.code, equalTo(400))
      }
    }
  }
})

private fun post(url: String, requestBody: String): Response {
  return OkHttpClient()
      .newCall(Request.Builder()
          .url(url)
          .post(requestBody.trimIndent().toRequestBody(JSON)
          )
          .build()
      ).execute()
}

val JSON = "application/json; charset=utf-8".toMediaType()