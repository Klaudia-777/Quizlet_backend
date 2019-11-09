package com.example.quizlet.controller

import com.example.quizlet.dao.TestDao
import com.example.quizlet.entities.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.stream.Stream
import javax.servlet.http.HttpServletResponse
import kotlin.streams.toList

@CrossOrigin
@RestController
class QuizletController(@Autowired val testDao: TestDao) {

    @PostMapping("/test")
    @Throws(IOException::class)
    fun uploadSubjectsFile(@RequestParam(name = "file") multipartFile: MultipartFile): String {
        return parseTestFile(multipartFile).run {
            testDao.save(this)
            id
        }
    }

    @GetMapping("/{testId}/results")
    @Throws(IOException::class)
    fun downloadResults(httpServletResponse: HttpServletResponse, @PathVariable testId: String) {
        generateFile(httpServletResponse, testDao.findById(testId).get())
    }

    @PostMapping("/{testId}/studentResult")
    fun addStudentResult(studentResult: StudentResult, @PathVariable testId: String) {
        testDao.findById(testId).ifPresent {
            it.studentResults.add(studentResult)
            testDao.save(it)
        }
    }

    @GetMapping("/{testId}/questions")
    fun sendQuestions(@PathVariable testId: String): List<Question> {
        println(testId)
        println(testDao.findById(testId))
        return testDao.findById(testId).map { it.questions }.orElse(mutableListOf())
    }
}

/*
UPLOADING TEST DATA FILE
 */

private fun parseRowToAnswer(row: List<String>): Answer {
    return Answer(text = row[0],
            isCorrect = row[1].toBoolean())
}

private fun parseRowToQuestion(row: List<String>): Question {
    return Question(question = row[0],
            answers = row.drop(1).chunked(2).map { parseRowToAnswer(it) }.toMutableList())
}

fun parseTestFile(multipartFile: MultipartFile): Test {

    val csvReader = BufferedReader(InputStreamReader(multipartFile.inputStream))
    var row: String
    val lines = csvReader.lines()
    val questions = lines.map { it.split(",") }
            .map { parseRowToQuestion(it) }.toList()
    return Test(questions = questions.toMutableList())
}


/*
DOWNLOADING RESULTS FILE
 */

private fun fillHeaders(questions: List<String>): List<String> {
    questions.forEach { _ -> println() }
    val questionsHeaders = questions.joinToString(";")
    return Stream.of("Numer Albumu", questionsHeaders, "Wynik").toList()
}

fun toCSVData(studentResults: StudentResult, questions: List<Question>): String {
    val albumNumber = studentResults.albumNumber
    val questionsIdAndAnwersMap = questions.associate { it.id to it.answers.filter { a -> a.isCorrect } }
    val studentAnswers = studentResults.choices
            .map { questionsIdAndAnwersMap.getValue(it.questionId).map { a -> a.id } == it.answers }.joinToString(";")

    return "$albumNumber;$studentAnswers\n"
}

fun generateFile(response: HttpServletResponse, test: Test) {

    val filename = "Wyniki.csv"
    val writer = response.writer

    val questionsObjects = test.questions
    questionsObjects.forEach { _ -> println() }
    val questionNames = questionsObjects.map { it.question }.toList()
    questionNames.forEach { _ -> println() }
    val headers = fillHeaders(questionNames)

    writer.write(headers.joinToString(";", "", "\n"))
    response.contentType = "text/csv"
    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"$filename\"")
    test.studentResults.forEach { writer.write(toCSVData(it, questionsObjects)) }

}


