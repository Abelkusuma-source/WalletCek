package com.app.walletcek.utils

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object FileProcessingUtils {

    suspend fun extractTextFromUri(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri) ?: ""

        return@withContext when {
            type.startsWith("image/") -> {
                extractTextFromImage(context, uri)
            }
            type == "application/pdf" -> {
                extractTextFromPdf(context, uri)
            }
            type == "text/plain" -> {
                extractTextFromTxt(context, uri)
            }
            else -> ""
        }
    }

    private suspend fun extractTextFromImage(context: Context, uri: Uri): String {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val result = recognizer.process(image).await()
            result.text
        } catch (e: Exception) {
            android.util.Log.e("FileProcessing", "Image processing failed", e)
            ""
        }
    }

    private fun extractTextFromPdf(context: Context, uri: Uri): String {
        return try {
            PDFBoxResourceLoader.init(context)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val document = PDDocument.load(inputStream)
                val stripper = PDFTextStripper()
                // Use default formatting to avoid issues with some PDF types
                val text = stripper.getText(document)
                document.close()
                text
            } ?: ""
        } catch (e: Exception) {
            android.util.Log.e("FileProcessing", "PDF processing failed", e)
            ""
        }
    }

    private fun extractTextFromTxt(context: Context, uri: Uri): String {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    reader.readText()
                }
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
