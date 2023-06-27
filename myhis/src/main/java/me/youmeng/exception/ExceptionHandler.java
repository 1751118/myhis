package me.youmeng.exception;

import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理类
 */
@Slf4j
@ControllerAdvice(annotations = {RestController.class, Controller.class})  //标明是控制类的通知，并接收括号内标记的类抛出的异常，统一到本类下处理
public class ExceptionHandler {

    /**
     * 异常处理，处理SQLIntegrityConstraintViolationException这个异常
     * @param ex
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.error(message);
        if(message.contains("Duplicate entry")){
            //表示不满足唯一性约束
            String[] words = ex.getMessage().split(" ");
            log.info("用户"+words[2]+"已经存在");
            return R.error("用户"+words[2]+"已经存在");
        }


        return R.error("未知错误");
    }
}
