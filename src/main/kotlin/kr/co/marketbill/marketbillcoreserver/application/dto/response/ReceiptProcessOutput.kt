package kr.co.marketbill.marketbillcoreserver.application.dto.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ReceiptProcessOutput(
    @SerialName("file_name")
    val fileName: String,
    @SerialName("file_path")
    val filePath: String,
    @SerialName("file_format")
    val fileFormat: String,
    @SerialName("metadata")
    val metadata: String,
)