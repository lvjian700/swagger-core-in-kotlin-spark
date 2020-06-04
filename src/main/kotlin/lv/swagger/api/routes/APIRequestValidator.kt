package lv.swagger.api.routes

import com.atlassian.oai.validator.OpenApiInteractionValidator
import com.atlassian.oai.validator.model.Request
import com.atlassian.oai.validator.model.SimpleRequest
import com.atlassian.oai.validator.report.ValidationReport
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules

class APIRequestValidator(
    swaggerSchema: String = loadSchema()
) {

  private val validator = OpenApiInteractionValidator
      .createForInlineApiSpecification(swaggerSchema)
      .withWhitelist(
          ValidationErrorsWhitelist.create().withRule(
              "Ignore missing security",
              WhitelistRules.messageHasKey("validation.request.security.missing")
          )
      )
      .build()

  fun validate(request: spark.Request) = request
      .let { buildRequest(it) }
      .let { validator.validateRequest(it) }
      .let { formatReport(it) }

  private fun formatReport(report: ValidationReport): List<APIError> =
      report.messages.map { APIError(it.message) }

  private fun buildRequest(request: spark.Request): Request = SimpleRequest.Builder
      .post(request.pathInfo())
      .withContentType(request.contentType())
      .withBody(request.body())
      .build()

}

fun loadSchema() = ClassLoader
    .getSystemResourceAsStream("lv-api-swagger.yaml")
    .bufferedReader()
    .use { it.readText() }