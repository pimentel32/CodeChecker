package com.checkerWeb.checker.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionAdvice {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public String handleMaxSizeException(Model model, MaxUploadSizeExceededException e) {
	  
    List<String> messages = new ArrayList<>();
    messages.add("Upload is too large! Please remain under 10MB");
    model.addAttribute("messages", messages);

    return "errorPage";
  }
}