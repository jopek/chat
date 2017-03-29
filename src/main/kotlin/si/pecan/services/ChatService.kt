package si.pecan.services

import org.springframework.stereotype.Service
import si.pecan.*
import si.pecan.dto.Message
import si.pecan.dto.toDto
import si.pecan.model.ChatRoom
import si.pecan.dto.ChatRoom as Dto
import si.pecan.model.InstantMessage
import si.pecan.model.User
import java.util.*
import javax.transaction.Transactional


@Service
class ChatService(private val userRepository: UserRepository,
                  private val chatRoomRepository: ChatRoomRepository,
                  private val instantMessageRepository: InstantMessageRepository) {


    @Transactional
    fun getOrCreateChat(initiatorUsername: String, targetUsername: String): Dto {
        val initiator = userRepository.findByUsername(initiatorUsername) ?: throw UserNotFound()
        val target = userRepository.findByUsername(targetUsername) ?: throw UserNotFound()
        val chatRoom = initiator.chatRooms.find { it.users.any { it == target } } ?: chatRoomRepository.save(ChatRoom().apply {
            users = arrayListOf(initiator, target)
            createdBy = initiator
        })

        return Dto(
                chatRoom.id!!,
                initiator.toDto(),
                target.toDto(),
                chatRoom.messages.map(InstantMessage::toDto),
                chatRoom.created,
                if (chatRoom.messages.isEmpty()) null else chatRoom.messages.last().created
        )
    }

    @Transactional
    fun postMessage(username: String, chatId: UUID, messageContent: String): Message {
        val chat = chatRoomRepository.findOne(chatId) ?: throw ChatNotFound()
        val user = chat.users.find { it.username == username } ?: throw UserNotAllowedToAccessChat()
        return instantMessageRepository.save(InstantMessage().apply {
            room = chat
            content = messageContent
            postedBy = user
        }).toDto().apply { this.chatId = chatId }
    }
}