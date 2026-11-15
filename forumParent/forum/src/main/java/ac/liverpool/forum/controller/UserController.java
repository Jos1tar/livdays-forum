package ac.liverpool.forum.controller;


import ac.liverpool.forum.entity.dto.UserRegisterDTO;
import ac.liverpool.forum.entity.VO.Result;
import ac.liverpool.forum.entity.Users;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UsersService usersService;

    // get user information by userId
    @GetMapping("/userInfo/{userId}")
    public Result userInfo(@PathVariable Long userId) {
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.USER_ID_MISSING);
        }
        return usersService.userInfo(userId);
    }

    // update user information - only the logged-in user can update their own profile
    @PutMapping("/userInfo/{userId}")
    public Result updateUserInfo(HttpServletRequest request, @PathVariable Long userId, @RequestBody Users user) {
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        if (!currentUserId.equals(userId)) {
            return Result.error(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        user.setId(currentUserId);
        log.info("update user info: userId={}", currentUserId);
        return usersService.updateUserInfo(user);
    }

    // get user posts by userId
    @GetMapping("/userInfo/{userId}/posts")
    public Result getMyPosts(@PathVariable Long userId) {
        log.info("retrieve user posts: userId={}", userId);
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.USER_ID_MISSING);
        }
        return usersService.getMyPosts(userId);
    }


    @PostMapping("/send-verification-code")
    public Result sendVerificationCode(@RequestParam String email) {
        usersService.sendVerificationCode(email);
        return Result.success();
    }

    @PostMapping("/register")
    public Result register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        usersService.register(registerDTO);
        return Result.success();
    }
}
