package lv.swagger.api.routes

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.isEmpty
import io.mockk.every
import io.mockk.mockk
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import spark.Request

object APIRequestValidatorSpek: Spek({
  describe("validate()") {

    context("given a valid request") {
      val request = createMockPostRequest("""
        {
          "name": "Kotlin",
          "quote": 10.0,
          "startAt": "2020-06-01"
        }
      """.trimIndent())

      it("returns empty") {
        val actual = APIRequestValidator(SWAGGER_SCHEMA).validate(request)

        assertThat(actual, isEmpty)
      }
    }


    context("given a request is missing required fields") {
      val request = createMockPostRequest("""
        {
          "startAt": "2020-06-01"
        }
      """.trimIndent())

      it("returns empty") {
        val actual = APIRequestValidator(SWAGGER_SCHEMA).validate(request)

        assertThat(actual, hasElement(
            APIError("Object has missing required properties ([\"name\",\"quote\"])")
        ))
      }
    }
  }
})

private fun createMockPostRequest(body: String) = mockk<Request>()
    .also { httpRequest ->
      every { httpRequest.pathInfo() } returns "/products"
      every { httpRequest.contentType() } returns "application/json"
      every { httpRequest.body() } returns body
      every { httpRequest.requestMethod() } returns "POST"
    }

private const val SWAGGER_SCHEMA = """
openapi: 3.0.1
info:
  title: Product API
  description: Creating products.
  contact:
    email: lvjian700@gmail.com
  license:
    name: MIT
    url: https://opensource.org/licenses/MIT
  version: "1.0"
paths:
  /products:
    post:
      operationId: routeProducts
      requestBody:
        description: Post Product
        content:
          application/json:
            schema:
              required:
                - name
                - quote
              properties:
                name:
                  type: string
                quote:
                  type: number
                  format: double
                startAt:
                  type: string
                  format: date
                endDate:
                  type: string
                  format: date
      responses:
        "204":
          description: default response
"""