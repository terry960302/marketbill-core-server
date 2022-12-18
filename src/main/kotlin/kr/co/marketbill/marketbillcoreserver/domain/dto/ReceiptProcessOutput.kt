package kr.co.marketbill.marketbillcoreserver.domain.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ReceiptProcessOutput(
    @JsonProperty("file_name")
    val fileName: String,
    @JsonProperty("file_path")
    val filePath: String,
    @JsonProperty("file_format")
    val fileFormat: String,
    @JsonProperty("metadata")
    val metadata: String,
) {
}