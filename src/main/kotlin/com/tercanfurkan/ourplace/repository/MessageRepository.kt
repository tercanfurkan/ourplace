package com.tercanfurkan.ourplace.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param

interface MessageRepository : CoroutineCrudRepository<Message, String> {

    // language=SQL
    @Query("""
        SELECT * FROM (
            SELECT * FROM MESSAGES
            ORDER BY "SENT_AT" DESC
            LIMIT 10
        ) ORDER BY "SENT_AT"
    """)
    fun findRecent(): Flow<Message>
}