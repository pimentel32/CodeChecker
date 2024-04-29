package com.checkerWeb.checker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.checkerWeb.checker.checkerBase.driver.Application;
import com.checkerWeb.checker.service.FilesStorageService;

import jakarta.annotation.Resource;

@SpringBootApplication
//public class CheckerApplication implements CommandLineRunner{
public class CheckerApplication{
	@Resource
	FilesStorageService storageService;
	
	public static void main(String[] args) {
	
		//String [] files = {"C:/Users/denis/NewEclipseWorkspace/checker/uploaded_files", ".java"};
		SpringApplication.run(CheckerApplication.class, args);
		//Application.run(files);
	}
	
//	@Override
//	 public void run(String... arg) throws Exception {
//	    storageService.init();
//	  }
	
}
