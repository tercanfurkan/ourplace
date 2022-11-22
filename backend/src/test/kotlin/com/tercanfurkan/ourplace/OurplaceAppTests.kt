package com.tercanfurkan.ourplace

import app.cash.turbine.test
import com.tercanfurkan.ourplace.repository.ContentType
import com.tercanfurkan.ourplace.repository.Message
import com.tercanfurkan.ourplace.repository.MessageRepository
import com.tercanfurkan.ourplace.service.MessageViewModel
import com.tercanfurkan.ourplace.service.UserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlow
import java.net.URI
import java.net.URL
import java.time.Instant
import kotlin.time.ExperimentalTime

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.r2dbc.url=r2dbc:h2:mem:///testdb;USER=user;PASSWORD=password"
    ]
)
class OurplaceAppTests(
    @Autowired val rsocketBuilder: RSocketRequester.Builder,
    @Autowired val messageRepository: MessageRepository,
    @LocalServerPort val port: Int
) {

    val now: Instant = Instant.now()
    val aSecondAgo: Instant by lazy { now.minusSeconds(1) }
    val twoSecondsAgo: Instant by lazy { now.minusSeconds(2) }

    @BeforeEach
    fun setUp() {
        runBlocking {
            populateDB()
        }
    }

    @AfterEach
    fun tearDown() {
        runBlocking {
            messageRepository.deleteAll()
        }
    }

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @Test
    fun `test that messages API streams recent messages`() {
        runBlocking {
            val rSocketRequester = rsocketBuilder.websocket(URI("ws://localhost:${port}/rsocket"))

            rSocketRequester
                .route("api.v1.messages.stream")
                .retrieveFlow<MessageViewModel>()
                .test {
                    assertThat(expectItem().copyForTesting())
                        .isEqualTo(
                            MessageViewModel(
                                "message1",
                                UserViewModel("user1", URL("http://link.com")),
                                now.minusSeconds(2)
                            )
                        )

                    assertThat(expectItem().copyForTesting())
                        .isEqualTo(
                            MessageViewModel(
                                "message2",
                                UserViewModel("user2", URL("http://link.com")),
                                now.minusSeconds(1)
                            )
                        )
                    assertThat(expectItem().copyForTesting())
                        .isEqualTo(
                            MessageViewModel(
                                "message3",
                                UserViewModel("user3", URL("http://link.com")),
                                now
                            )
                        )

                    expectNoEvents()

                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    suspend fun populateDB() {
        messageRepository.saveAll(
            listOf(
                Message(
                    "message1",
                    ContentType.PLAIN,
                    twoSecondsAgo,
                    "user1",
                    "http://link.com"
                ),
                Message(
                    "message2",
                    ContentType.PLAIN,
                    aSecondAgo,
                    "user2",
                    "http://link.com"
                ),
                Message(
                    "message3",
                    ContentType.PLAIN,
                    now,
                    "user3",
                    "http://link.com"
                )
            )
        ).collect()
    }
}
