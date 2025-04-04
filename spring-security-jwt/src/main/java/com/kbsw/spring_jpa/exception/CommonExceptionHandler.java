package com.kbsw.spring_jpa.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //예외 처리를 하나의 클래스에 모아서 할 수 있다. @ControllerAdvice를 붙인다
public class CommonExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model){

        model.addAttribute("errorMsg", e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

  /*  @ExceptionHandler(NumberFormatException.class)
    public String handleIllegalArgumentException2(Exception e, Model model){

        model.addAttribute("errorMsg", e.getMessage());

        return "error2";
    }
*/

}


