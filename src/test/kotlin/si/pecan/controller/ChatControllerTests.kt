package si.pecan.controller

import com.winterbe.expekt.should
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import si.pecan.dto.ChatRoom
import si.pecan.dto.Message
import si.pecan.dto.PostMessageRequest
import si.pecan.services.ChatService
import javax.transaction.Transactional

/**
 * Created by m.pecan on 22/03/2017.
 */

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
open class ChatControllerTests: ControllerTestBase() {

    @Autowired
    lateinit var chatService: ChatService


    @Test
    fun testGetChatWithUser(){
        val initiator = "user"
        val target = "target"

        val users = arrayOf(initiator, target).map(userService::getOrCreate)

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/chat")
                .param("username", initiator)
                .param("partner", target))
                .andExpect(status().isOk)
                .andExpect {
                    mapper.readValue(it.response.contentAsString, ChatRoom::class.java)
                            .should.be.an.instanceof(ChatRoom::class.java)
                }

    }

    @Test
    fun testPostMessage() {
        val (initiator, chatRoom) = createChatRoom()

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(
                        PostMessageRequest(initiator, chatRoom.id, "Some message content")
                ))
        ).andExpect(status().isOk)
                .andExpect{
                    mapper.readValue(it.response.contentAsString, Message::class.java)
                            .should.be.an.instanceof(Message::class.java)
                }
    }

     protected fun createChatRoom(): Pair<String, ChatRoom> {
        val initiator = "user"
        val target = "target"

        val users = arrayOf(initiator, target).map(userService::getOrCreate)

        val chatRoom = chatService.getOrCreateChat(initiator, target)
        return Pair(initiator, chatRoom)
    }


}