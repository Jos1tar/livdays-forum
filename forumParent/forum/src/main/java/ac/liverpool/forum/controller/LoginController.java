package ac.liverpool.forum.controller;

import ac.liverpool.forum.entity.LoginInfo;
import ac.liverpool.forum.entity.VO.Result;
import ac.liverpool.forum.entity.Users;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UsersService usersService;

    @PostMapping("/login")
    public Result login(@RequestBody Users user){
        log.info("User login, username={}", user.getUsername()); // never log the password
        LoginInfo loginInfo = usersService.login(user);
        if(loginInfo != null){
            return Result.success(loginInfo);
        }
        return Result.error(AppHttpCodeEnum.LOGIN_ERROR);
    }

    @PostMapping("/logout")//get the token from the request header and add it to the blacklist
    public Result logout(HttpServletRequest request) {
        return usersService.logout(request);
    }

}
