package supports

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.JsonObject
import lv.swagger.api.routes.gson

fun String.toJsonObject() = gson()
    .fromJson<JsonObject>(this)