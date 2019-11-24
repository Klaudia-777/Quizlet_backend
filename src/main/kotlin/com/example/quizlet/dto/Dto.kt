package com.example.quizlet.dto

import com.example.quizlet.entities.StudentChoice
import com.example.quizlet.entities.StudentResult

data class StudentResultDto(
        val albumNumber: String,
        val choices: MutableList<StudentChoiceDto> = mutableListOf(),
        val result: String
)

data class StudentChoiceDto(
        val questionId: String,
        val answers: MutableList<String> = mutableListOf()
)

fun StudentResultDto.toEntity() = StudentResult(albumNumber = albumNumber, result = result, choices = choices.map { it.toEntity() }.toMutableList())
private fun StudentChoiceDto.toEntity() = StudentChoice(questionId = questionId, answers = answers)