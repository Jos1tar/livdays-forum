package ac.liverpool.forum.entity;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 *
 *
 * @author makejava
 * @since 2025-03-21 18:31:30
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users  {
//
    private Long id;

    private String username;

    // accepted in request bodies (login) but never written to responses
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String nickname ;

    private String email;

    private String avatar;

    private String school;

    private String major;

    private String bio;

    private Date createdAt;

    private Integer isDeleted;


   //new value for register
    private String studentId;
    private String grade;
    private Boolean emailVerified;
    @JsonIgnore
    private String verificationCode;
    @JsonIgnore
    private LocalDateTime verificationCodeExpireTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;



}

