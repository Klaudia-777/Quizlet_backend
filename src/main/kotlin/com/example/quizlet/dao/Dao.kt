package com.example.quizlet.dao

import com.example.quizlet.entities.Test
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TestDao: CrudRepository<Test,String>
