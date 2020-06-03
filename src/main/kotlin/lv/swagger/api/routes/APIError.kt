package lv.swagger.api.routes

data class APIErrors(val errors: List<APIError>)
data class APIError(val message: String)
