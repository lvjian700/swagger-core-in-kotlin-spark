package e2e

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lv.swagger.api.main
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import spark.Spark
import supports.toJsonObject

object PostProductsSpek : Spek({

  describe("POST - /products") {
    val url = "http://localhost:4567/products"

    beforeGroup {
      main()
    }

    afterGroup {
      Spark.stop()
    }

    context("when send a valid post request") {
      lateinit var response: Response

      beforeEachTest {
        response = postRequest(url, """
                  {
                    "name": "Kotlin",
                    "quote": 10.0,
                    "startAt": "2020-06-01"
                  }
                  """)
      }

      it("returns 201") {


        assertThat(response.code, equalTo(201))
        assertThat(
            response.body!!.string().toJsonObject(),
            equalTo(
              """
                {
                  "name": "Kotlin",
                  "quote": 10.0,
                  "startAt": "2020-06-01"
                }
              """.toJsonObject()
            )
        )
      }
    }

    context("when missing required field") {
      lateinit var response: Response

      beforeEachTest {
        response = postRequest(url, """
            {
              "startAt": "2020-06-01"
            }
            """)
      }

      it("returns 400") {
        assertThat(response.code, equalTo(400))
      }
    }
  }
})

private fun postRequest(url: String, body: String): Response {
  return OkHttpClient()
      .newCall(Request.Builder()
          .url(url)
          .post(body.toRequestBody("application/json".toMediaTypeOrNull())
          )
          .build()
      ).execute()
}
