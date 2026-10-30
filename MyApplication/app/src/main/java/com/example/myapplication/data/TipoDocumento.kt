package com.example.myapplication.data

/** Formatos de documento permitidos. */
enum class TipoDocumento(val extension: String, val mimeType: String) {
    PDF("pdf", "application/pdf"),
    DOC("doc", "application/msword"),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    companion object {
        val MIMES_PERMITIDOS: Array<String> = entries.map { it.mimeType }.toTypedArray()

        fun desdeExtension(ext: String): TipoDocumento? =
            entries.firstOrNull { it.extension == ext.lowercase() }

        fun desdeMime(mime: String?): TipoDocumento? =
            entries.firstOrNull { it.mimeType == mime }
    }
}
