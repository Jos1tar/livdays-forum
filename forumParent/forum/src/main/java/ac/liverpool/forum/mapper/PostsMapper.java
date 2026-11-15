package ac.liverpool.forum.mapper;


import ac.liverpool.forum.entity.dto.PostQueryParam;
import ac.liverpool.forum.entity.Posts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PostsMapper {


    // Search for posts
    List<Posts> list(PostQueryParam postQueryParam);


    // Query post details based on post ID
    @Select("SELECT p.*, u.username\n" +
            "FROM posts p\n" +
            "JOIN users u ON p.user_id = u.id\n" +
            "WHERE p.id = #{id} AND p.is_deleted = 0")
    Posts getPostById(Long id);

    // image urls of one post, in upload order
    @Select("SELECT image_url FROM post_images WHERE post_id = #{postId} ORDER BY sort_order, id")
    List<String> getPostImages(Long postId);

    @Update("UPDATE posts SET views = IFNULL(views, 0) + 1 WHERE id = #{id}")
    int incrementViews(Long id);

    @Update("UPDATE posts SET comments = IFNULL(comments, 0) + 1 WHERE id = #{id}")
    int incrementComments(Long id);

    @Update("UPDATE posts SET likes = #{likes} WHERE id = #{id}")
    int updateLikes(@Param("id") Long id, @Param("likes") int likes);

    // Insert post (return self added ID)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertPost(Posts post);

    // Batch insertion of images
    int insertPostImages(@Param("postId") Long postId,
                         @Param("imageUrls") List<String> imageUrls);
}
