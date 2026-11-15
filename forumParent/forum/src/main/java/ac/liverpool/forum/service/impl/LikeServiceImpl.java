package ac.liverpool.forum.service.impl;

import ac.liverpool.forum.entity.VO.Result;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.mapper.CommentMapper;
import ac.liverpool.forum.mapper.LikeMapper;
import ac.liverpool.forum.mapper.PostsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class LikeServiceImpl implements ac.liverpool.forum.service.LikeService {
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private PostsMapper postsMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    @Transactional
    public Result like(Long userId, Long targetId, String type) {
        if (userId == null || targetId == null || !("post".equals(type) || "comment".equals(type))) {
            return Result.error(AppHttpCodeEnum.PARAMETER_ERROR, "targetId and type ('post' or 'comment') are required");
        }

        // make sure the target exists
        boolean exists = "post".equals(type)
                ? postsMapper.getPostById(targetId) != null
                : commentMapper.getById(targetId) != null;
        if (!exists) {
            return Result.error(AppHttpCodeEnum.NO_POSTS);
        }

        // Check if it has been liked
        if (likeMapper.isAlreadyLiked(userId, targetId, type)) {
            return Result.error(AppHttpCodeEnum.REPEAT_LIKE);
        }

        try {
            likeMapper.like(userId, targetId, type);
        } catch (DuplicateKeyException e) {
            // two clicks at the same time: the UNIQUE(user_id, target_id, type) key caught it
            return Result.error(AppHttpCodeEnum.REPEAT_LIKE);
        }

        // keep the cached counter on posts/comments in sync with the likes table
        int likeCount = likeMapper.countLikes(targetId, type);
        if ("post".equals(type)) {
            postsMapper.updateLikes(targetId, likeCount);
        } else {
            commentMapper.updateLikes(targetId, likeCount);
        }

        return Result.success(likeCount);
    }
}
