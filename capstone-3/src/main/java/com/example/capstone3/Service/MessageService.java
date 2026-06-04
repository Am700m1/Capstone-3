package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.MessageDTOIn;
import com.example.capstone3.DTO.Out.MessageDTOOut;
import com.example.capstone3.Models.Conversation;
import com.example.capstone3.Models.Message;
import com.example.capstone3.Repository.ConversationRepository;
import com.example.capstone3.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;

    public List<MessageDTOOut> getAll() {
        List<MessageDTOOut> messageDTOOuts = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            messageDTOOuts.add(convertToDTO(message));
        }
        return messageDTOOuts;
    }

    public MessageDTOOut getMessage(Integer id) {
        Message message = messageRepository.findMessageById(id);
        if (message == null) {
            throw new ApiException("Message not found");
        }
        return convertToDTO(message);
    }

    public void addMessage(MessageDTOIn messageDTOIn) {
        Conversation conversation = conversationRepository.findConversationById(messageDTOIn.getConversationId());
        if (conversation == null) {
            throw new ApiException("Conversation not found");
        }
        Message message = new Message();
        message.setConversation(conversation);
        message.setContent(messageDTOIn.getContent());
        messageRepository.save(message);
    }

    public void updateMessage(Integer id, MessageDTOIn messageDTOIn) {
        Message message = messageRepository.findMessageById(id);
        if (message == null) {
            throw new ApiException("Message not found");
        }
        Conversation conversation = conversationRepository.findConversationById(messageDTOIn.getConversationId());
        if (conversation == null) {
            throw new ApiException("Conversation not found");
        }
        message.setConversation(conversation);
        message.setContent(messageDTOIn.getContent());
        messageRepository.save(message);
    }

    public void deleteMessage(Integer id) {
        Message message = messageRepository.findMessageById(id);
        if (message == null) {
            throw new ApiException("Message not found");
        }
        messageRepository.deleteById(id);
    }

    public MessageDTOOut convertToDTO(Message message) {
        MessageDTOOut messageDTOOut = new MessageDTOOut();
        messageDTOOut.setId(message.getId());
        messageDTOOut.setConversationId(message.getConversation().getId());
        messageDTOOut.setContent(message.getContent());
        messageDTOOut.setSentAt(message.getSentAt());
        messageDTOOut.setIsRead(message.getIsRead());
        return messageDTOOut;
    }


    //^^^^^^^CRUD^^^^^^^^


}
