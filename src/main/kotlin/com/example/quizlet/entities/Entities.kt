package com.example.quizlet.entities

import java.util.*
import javax.persistence.*

@Entity
data class Question(@Id val id: String = generateId(), val question: String, @OneToMany(cascade = [CascadeType.ALL]) val answers: List<Answer> = listOf())

@Entity
data class Answer(@Id val id: String = generateId(), val text: String, val isCorrect: Boolean)

@Entity
data class Test(@Id val id: String = generateId(), @OneToMany(cascade = [CascadeType.ALL]) val questions: List<Question> = listOf(), @OneToMany(cascade = [CascadeType.ALL]) val studentResults: List<StudentResult> = listOf())

@Entity
data class StudentResult(@Id val id: String = generateId(), val albumNumber: String, @OneToMany(cascade = [CascadeType.ALL]) val choices: List<StudentChoice> = listOf())

@Entity
data class StudentChoice(@Id val id: String = generateId(), val questionId: String,
                         @ElementCollection
                         @CollectionTable(name = "studentAnswers", joinColumns = [JoinColumn(name = "studentChoiceId")])
                         @Column(name = "ANSWER_ID") val answers: List<String> = listOf())


fun generateId() = UUID.randomUUID().toString()