package com.example.quizlet.controller

import com.example.quizlet.dao.TestDao
import com.example.quizlet.entities.Answer
import com.example.quizlet.entities.Question
import com.example.quizlet.entities.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.streams.toList

@RestController
class QuizletController(@Autowired val testDao: TestDao) {

    @PostMapping("/test")
    @Throws(IOException::class)
    fun uploadSubjectsFile(@RequestParam(name = "file") multipartFile: MultipartFile) : String {
        return parseTestFile(multipartFile).run {
            testDao.save(this)
            id
        }
    }
}

private fun parseRowToAnswer(row: List<String>): Answer {
    return Answer(text = row[0],
            isCorrect = row[1].toBoolean())
}

private fun parseRowToQuestion(row: List<String>): Question {
    return Question(question = row[0],
            answers = row.drop(1).chunked(2).map { parseRowToAnswer(it) })
}

fun parseTestFile(multipartFile: MultipartFile): Test {

    val csvReader = BufferedReader(InputStreamReader(multipartFile.inputStream))
    var row: String
    val lines = csvReader.lines()
    val questions = lines.map { it.split(";") }
            .map { parseRowToQuestion(it) }.toList()
    return Test(questions = questions)
}