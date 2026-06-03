package com.example.capstone3.Repository;

import com.example.capstone3.Models.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    Conversation findConversationById(Integer id);
}
