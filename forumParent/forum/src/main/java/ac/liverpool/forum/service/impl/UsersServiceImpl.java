package ac.liverpool.forum.service.impl;

import ac.liverpool.forum.entity.dto.UserRegisterDTO;
import ac.liverpool.forum.entity.LoginInfo;
import ac.liverpool.forum.entity.Posts;
import ac.liverpool.forum.entity.VO.Result;
import ac.liverpool.forum.entity.Users;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import ac.liverpool.forum.exception.BusinessException;
import ac.liverpool.forum.mapper.UsersMapper;
import ac.liverpool.forum.service.EmailService;
import ac.liverpool.forum.service.TokenBlacklistService;
import ac.liverpool.forum.service.UsersService;
import ac.liverpool.forum.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class UsersServiceImpl implements UsersService {
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginInfo login(Users emp) {
        if (emp == null || !StringUtils.hasText(emp.getUsername()) || !StringUtils.hasText(emp.getPassword())) {
            return null;
        }
        Users usersLogin = usersMapper.findByUsername(emp.getUsername());
        if (usersLogin == null || !passwordMatches(emp.getPassword(), usersLogin.getPassword())) {
            return null;
        }
        // a record created by send-verification-code but never completed is not a real account
        if (!Boolean.TRUE.equals(usersLogin.getEmailVerified()) && usersLogin.getVerificationCode() != null) {
            return null;
        }

        // 1. generate JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", usersLogin.getUsername()); // add additional claims
        claims.put("id", usersLogin.getId());
        String jwt = JwtUtils.generateJwt(usersLogin.getId(), claims); // Passing userId and additional claims

        // 2. Return login information
        return new LoginInfo(Math.toIntExact(usersLogin.getId()), usersLogin.getUsername(), jwt);
    }

    /**
     * Passwords are stored as BCrypt hashes. Old demo rows in forumCopy.sql still hold plain text,
     * so those are compared directly to keep the sample accounts usable.
     */
    private boolean passwordMatches(String raw, String stored) {
        if (stored == null) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return passwordEncoder.matches(raw, stored);
        }
        return stored.equals(raw);
    }

    @Override
    public Result logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasLength(authHeader) && authHeader.startsWith("Bearer ")) {
            tokenBlacklistService.addToBlacklist(authHeader.substring(7));
            log.info("User logged out, token added to blacklist");
        }
        return Result.success("Logout successful");
    }

    @Override
    public Result userInfo(Long userId) {
        Users user = usersMapper.getUserById(userId);
        if (user == null) {
            return Result.error(AppHttpCodeEnum.USER_NOT_EXIST);
        }
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result updateUserInfo(Users user) {
        // 1. Parameter verification
        if (user == null || user.getId() == null) {
            return Result.error(AppHttpCodeEnum.USER_ID_MISSING);
        }

        // 2. Check if the user exists
        Users existingUser = usersMapper.getUserById(user.getId());
        if (existingUser == null) {
            return Result.error(AppHttpCodeEnum.USER_NOT_EXIST);
        }

        // 3. Username must stay unique
        if (StringUtils.hasText(user.getUsername()) && usersMapper.existsByUsername(user.getUsername(), user.getId())) {
            return Result.error(AppHttpCodeEnum.USERNAME_EXIST);
        }

        // 4. The profile endpoint must not change password / verification state
        user.setPassword(null);
        user.setEmailVerified(null);
        user.setVerificationCode(null);
        user.setVerificationCodeExpireTime(null);
        user.setUpdateTime(LocalDateTime.now());

        int updated = usersMapper.updateUserInfo(user);
        if (updated <= 0) {
            return Result.error(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        return Result.success("User information updated successfully");
    }

    @Override
    public Result getMyPosts(Long userId) {
        // an empty list is a normal result, not an error
        List<Posts> posts = usersMapper.getPostsByUserId(userId);
        return Result.success(posts);
    }

    // Send verification code to email and create a temporary user record as part of the registration process
    @Override
    public void sendVerificationCode(String email) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException("Email cannot be empty");
        }

        /*// Check if the email is a UK university email (ends with .ac.uk)
        if (!email.toLowerCase().endsWith(".ac.uk")) {
            throw new BusinessException("Only UK university email addresses (.ac.uk) are allowed");
        }*/

        // Generate a 6-digit random verification code
        String verificationCode = String.format("%06d", RANDOM.nextInt(1000000));

        Users user = usersMapper.findByEmail(email);
        if (user != null && Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new BusinessException(AppHttpCodeEnum.EMAIL_EXIST, "This email has already been registered");
        }

        if (user == null) {
            // Create a new temporary user record (no password until registration is completed)
            user = new Users();
            user.setUsername(email); // use email as username temporarily
            user.setPassword(null);
            user.setEmail(email);
            user.setVerificationCode(verificationCode);
            user.setVerificationCodeExpireTime(LocalDateTime.now().plusMinutes(5));
            user.setEmailVerified(false);
            user.setCreatedAt(new Date());
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            usersMapper.insert(user);
        } else {
            // registration was started before: just issue a new code
            user.setVerificationCode(verificationCode);
            user.setVerificationCodeExpireTime(LocalDateTime.now().plusMinutes(5));
            user.setUpdateTime(LocalDateTime.now());
            usersMapper.updateVerificationCode(user);
        }

        //Send verification code email
        emailService.sendVerificationCode(email, verificationCode);
    }

    @Override
    @Transactional
    public void register(UserRegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        // Verify email verification code
        Users user = usersMapper.findByEmail(registerDTO.getEmail());
        if (user == null || user.getVerificationCode() == null) {
            throw new BusinessException("Please send the verification code first");
        }
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new BusinessException(AppHttpCodeEnum.EMAIL_EXIST, "This email has already been registered");
        }
        if (!user.getVerificationCode().equals(registerDTO.getVerificationCode())) {
            throw new BusinessException("Verification code is incorrect");
        }
        if (user.getVerificationCodeExpireTime() == null
                || user.getVerificationCodeExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Verification code has expired");
        }

        // Verify the username is not taken by another account (the temp record itself uses the email as username)
        Users sameName = usersMapper.findByUsername(registerDTO.getUsername());
        if (sameName != null && !sameName.getId().equals(user.getId())) {
            throw new BusinessException(AppHttpCodeEnum.USERNAME_EXIST, "Username already exists");
        }

        // Update user information
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setMajor(registerDTO.getMajor());
        user.setUpdateTime(LocalDateTime.now());

        usersMapper.updateUserInfo(user);
        usersMapper.updateEmailVerified(user.getId());
    }

}
