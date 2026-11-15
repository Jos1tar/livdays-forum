package ac.liverpool.forum.controller;

import ac.liverpool.forum.entity.dto.MessageDTO;
import ac.liverpool.forum.entity.VO.Result;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Private messages. The current user is always taken from the JWT (set by TokenInterceptor),
 * never from request parameters, so nobody can read another user's messages.
 */
@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    private Long currentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    /**
     *send message
     */
    @PostMapping("/send")
    public Result sendMessage(HttpServletRequest request, @RequestBody MessageDTO messageDTO) {
        Long userId = currentUserId(request);
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        return messageService.sendMessage(userId, messageDTO);
    }

    /**
     * get message list of the target user
     */
    @GetMapping("/list/{targetUserId}")
    public Result getMessages(HttpServletRequest request,
                              @PathVariable Long targetUserId,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer pageSize) {
        Long userId = currentUserId(request);
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        return messageService.getMessages(userId, targetUserId, page, pageSize);
    }

    /**
     * get conversation list of the current user
     */
    @GetMapping("/conversations")
    public Result getConversations(HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        return messageService.getConversations(userId);
    }

    /**
     * mark message as read
     */
    @PutMapping("/read/{conversationId}")
    public Result markAsRead(HttpServletRequest request, @PathVariable Long conversationId) {
        Long userId = currentUserId(request);
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        return messageService.markAsRead(userId, conversationId);
    }

    /**
     *get unread message count
     */
    @GetMapping("/unread")
    public Result getUnreadCount(HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        return messageService.getUnreadCount(userId);
    }

    /**
     * delete conversation
     */
    @DeleteMapping("/{conversationId}")
    public Result deleteConversation(HttpServletRequest request, @PathVariable Long conversationId) {
        Long userId = currentUserId(request);
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        return messageService.deleteConversation(userId, conversationId);
    }
}
