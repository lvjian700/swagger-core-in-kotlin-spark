package e2e

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.JsonObject
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lv.swagger.api.main
import lv.swagger.api.routes.gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import spark.Spark

object PostProductsSpek : Spek({

  describe("POST - /products") {
    val url = "http://localhost:4567/products"

    context("when send a get request") {
      beforeGroup {
        main()
      }

      afterGroup {
        Spark.stop()
      }

      it("returns hello, world") {
        val response = OkHttpClient()
            .newCall(Request.Builder()
                .url(url)
                .post("""
                  {
                    "name": "Kotlin",
                    "quote": 10.0,
                    "startAt": "2020-06-01"
                  }
                  """.toRequestBody("application/json".toMediaTypeOrNull())
                )
                .build()
            ).execute()

        assertThat(
            gson().fromJson<JsonObject>(response.body!!.string()),
            equalTo(gson().fromJson<JsonObject>("""
                  {
                    "name": "Kotlin",
                    "quote": 10.0,
                    "startAt": "2020-06-01"
                  }
                  """)
            )
        )
      }
    }
  }
})