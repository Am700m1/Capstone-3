package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.DTO.In.MessageDTOIn;
import com.example.capstone3.DTO.Out.MessageDTOOut;
import com.example.capstone3.Enums.MessageSenderType;
import com.example.capstone3.Models.Apartment;
import com.example.capstone3.Models.Conversation;
import com.example.capstone3.Models.Message;
import com.example.capstone3.Models.Owner;
import com.example.capstone3.Models.User;
import com.example.capstone3.Repository.ApartmentRepository;
import com.example.capstone3.Repository.ConversationRepository;
import com.example.capstone3.Repository.MessageRepository;
import com.example.capstone3.Repository.OwnerRepository;
import com.example.capstone3.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final ApartmentRepository apartmentRepository;

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

    @Transactional
    public void addMessage(MessageDTOIn messageDTOIn) {
        Conversation conversation = resolveConversation(messageDTOIn);
        validateSender(conversation, messageDTOIn.getSenderType(), messageDTOIn.getSenderId());

        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderType(messageDTOIn.getSenderType());
        message.setSenderId(messageDTOIn.getSenderId());
        message.setContent(messageDTOIn.getContent());
        messageRepository.save(message);
    }

    public void updateMessage(Integer id, MessageDTOIn messageDTOIn) {
        Message message = messageRepository.findMessageById(id);
        if (message == null) {
            throw new ApiException("Message not found");
        }
        if (messageDTOIn.getConversationId() != null
                && !message.getConversation().getId().equals(messageDTOIn.getConversationId())) {
            throw new ApiException("Message cannot be moved to another conversation");
        }
        if (message.getSenderType() != messageDTOIn.getSenderType()
                || !message.getSenderId().equals(messageDTOIn.getSenderId())) {
            throw new ApiException("Message sender cannot be changed");
        }
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
        messageDTOOut.setSenderType(message.getSenderType());
        messageDTOOut.setSenderId(message.getSenderId());
        messageDTOOut.setContent(message.getContent());
        messageDTOOut.setSentAt(message.getSentAt());
        messageDTOOut.setIsRead(message.getIsRead());
        return messageDTOOut;
    }

    private Conversation resolveConversation(MessageDTOIn dto) {
        if (dto.getConversationId() != null) {
            Conversation conversation = conversationRepository.findConversationById(dto.getConversationId());
            if (conversation == null) {
                throw new ApiException("Conversation not found");
            }
            return conversation;
        }

        if (dto.getUserId() == null || dto.getOwnerId() == null || dto.getApartmentId() == null) {
            throw new ApiException("User ID, owner ID, and apartment ID are required for the first message");
        }

        Conversation existing = conversationRepository.findByUser_IdAndOwner_IdAndApartment_Id(
                dto.getUserId(), dto.getOwnerId(), dto.getApartmentId());
        if (existing != null) {
            return existing;
        }

        User user = userRepository.findUserById(dto.getUserId());
        Owner owner = ownerRepository.findOwnerById(dto.getOwnerId());
        Apartment apartment = apartmentRepository.findApartmentById(dto.getApartmentId());

        if (user == null) throw new ApiException("User not found");
        if (owner == null) throw new ApiException("Owner not found");
        if (apartment == null) throw new ApiException("Apartment not found");
        if (!apartment.getOwner().getId().equals(owner.getId())) {
            throw new ApiException("Owner does not own this apartment");
        }

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.setOwner(owner);
        conversation.setApartment(apartment);
        return conversationRepository.save(conversation);
    }

    private void validateSender(Conversation conversation, MessageSenderType senderType, Integer senderId) {
        if (senderType == MessageSenderType.USER
                && !conversation.getUser().getId().equals(senderId)) {
            throw new ApiException("Sender is not the user in this conversation");
        }
        if (senderType == MessageSenderType.OWNER
                && !conversation.getOwner().getId().equals(senderId)) {
            throw new ApiException("Sender is not the owner in this conversation");
        }
    }

    //^^^^^^^CRUD^^^^^^^^


}
