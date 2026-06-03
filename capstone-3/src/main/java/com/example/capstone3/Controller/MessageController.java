package com.example.capstone3.Controller;

import com.example.capstone3.Api.ApiResponse;
import com.example.capstone3.DTO.In.MessageDTOIn;
import com.example.capstone3.Service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/get")
    public ResponseEntity<?> getMessages() {
        return ResponseEntity.status(200).body(messageService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMessage(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(messageService.getMessage(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMessage(@RequestBody @Valid MessageDTOIn messageDTOIn) {
        messageService.addMessage(messageDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Message added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Integer id, @RequestBody @Valid MessageDTOIn messageDTOIn) {
        messageService.updateMessage(id, messageDTOIn);
        return ResponseEntity.status(200).body(new ApiResponse("Message updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer id) {
        messageService.deleteMessage(id);
        return ResponseEntity.status(200).body(new ApiResponse("Message deleted successfully"));
    }
}
