package e2e

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import lv.swagger.api.main
import okhttp3.OkHttpClient
import okhttp3.Request
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import spark.Spark

object GetHelloSpek : Spek({

  describe("GET - /hello") {
    val url = "http://localhost:4567/hello"

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
                .get()
                .build()
            ).execute()

        assertThat(response.body?.string(), equalTo("hello world!"))
      }
    }
  }
})