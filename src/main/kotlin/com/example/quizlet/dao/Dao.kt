package com.example.quizlet.dao

import com.example.quizlet.entities.Test
import org.springframework.data.repository.CrudRepository

interface TestDao: CrudRepository<Test,String>