package lv.swagger.api.routes

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.toJson
import com.google.gson.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import spark.Spark.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.ws.rs.POST
import javax.ws.rs.Path

@POST
@Path("/products")
@Operation(
    requestBody = RequestBody(
        description = "Post Product",
        content = arrayOf(
            Content(
                mediaType = "application/json",
                schema = Schema(
                    implementation = Product::class
                )
            )
        )
    )
)
fun routeProducts() {
  post("/products") { req, resp ->
    val product = gson()
        .fromJson<Product>(req.body())

    resp.status(201)
    resp.header("Content-Type", "application/json")
    gson().toJson(product)
  }
}

object LocalDateDeserializer: JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
  override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate? {
    return LocalDate.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE)
  }

  override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
    return src.format(DateTimeFormatter.ISO_LOCAL_DATE).toJson()
  }
}

private fun gson() = GsonBuilder()
    .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer)
    .create()

data class Product(
    val name: String,
    val quote: Double,
    val startAt: LocalDate,
    val endDate: LocalDate
)