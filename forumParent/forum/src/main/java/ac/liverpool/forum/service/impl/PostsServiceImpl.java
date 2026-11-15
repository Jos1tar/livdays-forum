package ac.liverpool.forum.service.impl;

import ac.liverpool.forum.entity.dto.PostCreateRequest;
import ac.liverpool.forum.entity.dto.PostQueryParam;
import ac.liverpool.forum.entity.*;
import ac.liverpool.forum.entity.VO.PageResult;
import ac.liverpool.forum.entity.VO.PostsVo;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.exception.BusinessException;
import ac.liverpool.forum.mapper.PostsMapper;
import ac.liverpool.forum.mapper.UsersMapper;
import ac.liverpool.forum.service.PostsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostsServiceImpl implements PostsService {
    @Autowired
    private PostsMapper postsMapper;

    @Autowired
    private UsersMapper usersMapper;


    // Implementation of pagination and conditional queries
    @Override
    public PageResult getPosts(PostQueryParam postQueryParam) {
        // start paging
        PageHelper.startPage(postQueryParam.getPage(), postQueryParam.getPageSize());
        //look up the post list
        List<Posts> posts = postsMapper.list(postQueryParam);
        //Convert Posts to PostsVo
        List<PostsVo> postsVoList = posts.stream()
                .map(postList -> {
                    PostsVo postsVo = new PostsVo();
                    BeanUtils.copyProperties(postList, postsVo); // Bean 拷贝
                    return postsVo;
                })
                .collect(Collectors.toList());

        // Get pagination information
        Page<Posts> p = (Page<Posts>) posts;

        // Return pagination results
        return new PageResult(p.getTotal(), postsVoList);
    }

    // Query post details based on post ID (also counts one view and loads the images)
    @Override
    public Posts getPostById(Long id) {
        Posts post = postsMapper.getPostById(id);
        if (post == null) {
            throw new BusinessException(AppHttpCodeEnum.NO_POSTS, "Post not found");
        }
        postsMapper.incrementViews(id);
        post.setViews((post.getViews() == null ? 0 : post.getViews()) + 1);
        post.setImageUrls(postsMapper.getPostImages(id));
        return post;
    }

    // Create a post
    @Override
    public Posts createPost(PostCreateRequest request) {
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BusinessException("Post content cannot be empty");
        }
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException("Post title cannot be empty");
        }
        // 1. Check if the user exists
        String username = usersMapper.getUsernameById(request.getUserId());
        if (username == null) {
            throw new BusinessException(AppHttpCodeEnum.USER_NOT_EXIST, "User not found");
        }

        // 2. create a post
        Posts post = new Posts();
        post.setUserId(request.getUserId());
        post.setUsername(username); // Set the username
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCreatedAt(new Date());

        postsMapper.insertPost(post);

        // 3. Insert post images if provided
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            postsMapper.insertPostImages(post.getId(), request.getImageUrls());
            post.setImageUrls(request.getImageUrls());
        }

        return post;
    }
}
