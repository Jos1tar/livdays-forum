package ac.liverpool.forum.exception;

import ac.liverpool.forum.entity.VO.Result;
import ac.liverpool.forum.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Turns exceptions into the unified {code, msg, data} response so the frontend can show a proper message.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusiness(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    // @Valid failures on @RequestBody (MethodArgumentNotValidException extends BindException)
    @ExceptionHandler(BindException.class)
    public Result handleValidation(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : AppHttpCodeEnum.PARAMETER_ERROR.getMsg();
        return Result.error(AppHttpCodeEnum.PARAMETER_ERROR, msg);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error(AppHttpCodeEnum.PARAMETER_ERROR, "Missing parameter: " + e.getParameterName());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicate(DuplicateKeyException e) {
        log.warn("Duplicate key: {}", e.getMessage());
        return Result.error(AppHttpCodeEnum.PARAMETER_ERROR, "This record already exists");
    }

    @ExceptionHandler(Exception.class)
    public Result handleOther(Exception e) throws Exception {
        if (e instanceof ErrorResponse) {
            // 404 / 405 / 415 etc. keep their normal HTTP status
            throw e;
        }
        log.error("Unhandled exception", e);
        return Result.error(AppHttpCodeEnum.SYSTEM_ERROR);
    }
}
