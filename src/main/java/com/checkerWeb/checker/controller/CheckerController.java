package com.checkerWeb.checker.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.checkerWeb.checker.checkerBase.driver.Application;
import com.checkerWeb.checker.model.UserSelection;
import com.checkerWeb.checker.service.FilesStorageService;

@Controller
public class CheckerController {
	
	@Autowired
	FilesStorageService storageService;
	
	@Value("${input_loc}")
	private String input_location;
	
	@Value("${output_file}")
	private String output_file;
	
	//Retrieves user input
	@GetMapping("/")
    public String retrieveUserInput(Model model) {	
		storageService.deleteAll();
		storageService.init();
		
		String[] progLang = {".java", ".cpp"};
	    model.addAttribute("progLang", progLang); 
	    model.addAttribute("input", new UserSelection());

	    return "upload_files";
    }
	
	//Checks file extension and sends it to the checker base for processing
	@PostMapping("/")
	public String uploadFiles(@RequestParam("files") MultipartFile[] files, @ModelAttribute("input") UserSelection input, Model model) {
		
		String[] files_args = {input_location, input.getLanguage()};
		List<String> messages = new ArrayList<>();
		
		Arrays.asList(files).stream().forEach(file -> {
			
			int index = file.getOriginalFilename().lastIndexOf('.');
			String extension = file.getOriginalFilename().substring(index + 1);
			
			try {	
				if(index < 0) {
					messages.add("No file uploaded");
				}else {
					if (input.getLanguage().equals("." + extension)) {
						storageService.save(file);
						messages.add(file.getOriginalFilename() + "  -  <Successful>");
						Application.run(files_args, input.getPercent(), output_file);
					}else {
						messages.add(file.getOriginalFilename() + "  -  <Invalid Extension>");
					}
				}
			 } catch (Exception e) {
		         messages.add(file.getOriginalFilename() + "  -  <Failed>  -  " + e.getMessage());
		     }
	     });
		
		model.addAttribute("messages", messages);
	    return "result";		  
	}
	
	 /* Adapted from https://www.baeldung.com/spring-controller-return-image-file
	  * Downloads a text file with comparison result*/
	@RequestMapping("/download")
	public ResponseEntity<ByteArrayResource> downloadComparison() throws IOException {
	
		Path root = Paths.get(output_file);
			
		byte[] exportedFileData = Files.readAllBytes(root);
		ByteArrayResource byteArrayResource = new ByteArrayResource(exportedFileData);
			
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "similarity_report.txt")
				.contentType(MediaType.TEXT_PLAIN)
				.contentLength(exportedFileData.length)
				.body(byteArrayResource);	
	}
}