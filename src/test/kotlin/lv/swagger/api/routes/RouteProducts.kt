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
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import spark.Spark
import spark.Spark.port
import spark.Spark.stop

object RouteProducts: Spek({
  describe("Products") {
    val port = 8088
    val url = "http://localhost:$port/test/products"

    context("when post product") {
      beforeGroup {
        port(port)
        Spark.path("/test") {
          routeProducts()
        }
      }

      afterGroup {
        stop()
      }

      val expected = JsonObject().apply {
        addProperty("name", "Kotlin")
        addProperty("quote", 100.00)
        addProperty("startAt", "2020-06-01")
        addProperty("endDate", "2020-07-01")
      }

      it("creates product") {
        val response = OkHttpClient()
            .newCall(Request.Builder()
                .url(url)
                .post("""{
                  "name": "Kotlin",
                  "quote": 100.00,
                  "startAt": "2020-06-01",
                  "endDate": "2020-07-01"
                }""".trimIndent().toRequestBody(JSON)
                )
                .build()
            ).execute()

        assertThat(response.code, equalTo(201))

        val jsonBody = Gson().fromJson<JsonObject>(response.body?.string()!!)
        assertThat(jsonBody, equalTo(expected))
      }
    }
  }
})

val JSON = "application/json; charset=utf-8".toMediaType()