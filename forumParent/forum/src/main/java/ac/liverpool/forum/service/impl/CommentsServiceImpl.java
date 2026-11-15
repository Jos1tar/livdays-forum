package ac.liverpool.forum.service.impl;

import ac.liverpool.forum.entity.dto.CommentQueryParam;
import ac.liverpool.forum.entity.Comments;
import ac.liverpool.forum.entity.VO.PageResult;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.exception.BusinessException;
import ac.liverpool.forum.mapper.CommentMapper;
import ac.liverpool.forum.mapper.PostsMapper;
import ac.liverpool.forum.service.CommentsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Comments Table Service Implementation Class
 *
 * @author makejava
 * @since 2025-03-23 17:51:13
 */
@Slf4j
@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostsMapper postsMapper;

    @Override
    public PageResult getComment(CommentQueryParam commentQueryParam) {
        log.info("Query request parameters {}", commentQueryParam);
        if (commentQueryParam.getPostId() == null) {
            throw new BusinessException("postId is required");
        }

        // start paging
        PageHelper.startPage(commentQueryParam.getPage(), commentQueryParam.getPageSize());
        // Top-level comments of this post (parent_id is NULL, or 0 in some old demo rows)
        List<Comments> comments = commentMapper.listByPostId(commentQueryParam.getPostId(), null);

        Page<Comments> p = (Page<Comments>) comments;
        return new PageResult(p.getTotal(), comments);
    }

    @Override
    public List<Comments> getChildComments(Long parentId) {
        log.info("Search for child comments with parent comment ID={}", parentId);
        return commentMapper.listByParentId(parentId);
    }

    @Override
    @Transactional
    public void addComment(Comments comment) {
        log.info("New comment: {}", comment);
        if (comment.getPostId() == null || comment.getContent() == null || comment.getContent().isBlank()) {
            throw new BusinessException("postId and content are required");
        }
        if (postsMapper.getPostById(comment.getPostId()) == null) {
            throw new BusinessException(AppHttpCodeEnum.NO_POSTS, "Post not found");
        }
        if (comment.getParentId() != null && comment.getParentId() == 0) {
            comment.setParentId(null); // treat 0 as "no parent"
        }
        commentMapper.addComment(comment);
        postsMapper.incrementComments(comment.getPostId());
    }
}
