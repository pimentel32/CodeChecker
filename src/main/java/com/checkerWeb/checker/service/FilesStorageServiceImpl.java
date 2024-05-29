/* 
 * Original code from https://www.bezkoder.com/thymeleaf-multiple-file-upload/
 */
package com.checkerWeb.checker.service;

import java.io.IOException;

import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService{

	@Value("${input_loc}")
	private String input_location;
	
	@Value("${output_loc}")
	private String output_location;
	
	@Override
	public void init() {
		 try {
		      Files.createDirectories(Paths.get(input_location));
		    } catch (IOException e) {
		      throw new RuntimeException("Could not initialize folder for upload!");
		    }
	}

	@Override
	public void save(MultipartFile file) {
		 try {
			  Path root = Paths.get(input_location);
		      Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()));
		      
		    } catch (Exception e) {
		      if (e instanceof FileAlreadyExistsException) {
		        throw new RuntimeException("Filename already exists.");
		      }

		      throw new RuntimeException(e.getMessage());
		    }
	}

	@Override
	public Resource load(String filename) {
		 try {
		      Path file = Paths.get(input_location).resolve(filename);
		      Resource resource = new UrlResource(file.toUri());

		      if (resource.exists() || resource.isReadable()) {
		        return resource;
		      } else {
		        throw new RuntimeException("Could not read the file!");
		      }
		    } catch (MalformedURLException e) {
		      throw new RuntimeException("Error: " + e.getMessage());
		    }
	}
	
	@Override
	public void deleteAll() {
		 FileSystemUtils.deleteRecursively(Paths.get(input_location).toFile());
		 FileSystemUtils.deleteRecursively(Paths.get(output_location).toFile());
	} 
}
