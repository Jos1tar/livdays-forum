package ac.liverpool.forum.controller;


import ac.liverpool.forum.entity.dto.LikeRequest;
import ac.liverpool.forum.entity.VO.Result;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping
    public Result like(HttpServletRequest request, @RequestBody LikeRequest likeRequest) {
        // the liker is always the logged-in user; a userId in the body is ignored
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(AppHttpCodeEnum.NEED_LOGIN);
        }
        return likeService.like(userId, likeRequest.getTargetId(), likeRequest.getType());
    }
}
