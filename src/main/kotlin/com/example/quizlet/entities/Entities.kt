package com.example.quizlet.entities

import org.apache.commons.lang3.RandomStringUtils
import java.util.*
import javax.persistence.*

@Entity
data class Question(@Id val id: String = generateId(),
                    val question: String,
                    @OneToMany(cascade = [CascadeType.ALL]) val answers: MutableList<Answer> = mutableListOf())

@Entity
data class Answer(@Id val id: String = generateId(),
                  val text: String,
                  val correctOrNot: Boolean)

@Entity
data class Test(@Id val id: String = generateId(),
                var noQuestionsToSend: Int = 0,
                @OneToMany(cascade = [CascadeType.ALL]) val questions: MutableList<Question> = mutableListOf(),
                @OneToMany(cascade = [CascadeType.ALL]) val studentResults: MutableList<StudentResult> = mutableListOf())

@Entity
data class StudentResult(@Id val id: String = generateId(),
                         val albumNumber: String,
                         val result: String,
                         @OneToMany(cascade = [CascadeType.ALL]) val choices: MutableList<StudentChoice> = mutableListOf())

@Entity
data class StudentChoice(@Id val id: String = generateId(),
                         val questionId: String,
                         @ElementCollection
                         @CollectionTable(name = "studentAnswers", joinColumns = [JoinColumn(name = "studentChoiceId")])
                         @Column(name = "ANSWER_ID") val answers: MutableList<String> = mutableListOf())


fun generateId() = RandomStringUtils.randomAlphanumeric(8)