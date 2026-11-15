package ac.liverpool.forum.exception;

import ac.liverpool.forum.enums.AppHttpCodeEnum;
import lombok.Getter;

/**
 * Expected business error whose message is safe to show to the user.
 * Converted to a Result by GlobalExceptionHandler.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final AppHttpCodeEnum code;

    public BusinessException(AppHttpCodeEnum code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(AppHttpCodeEnum.PARAMETER_ERROR, message);
    }
}
