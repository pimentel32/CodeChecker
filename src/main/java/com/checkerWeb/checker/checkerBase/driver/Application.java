package com.checkerWeb.checker.checkerBase.driver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.checkerWeb.checker.checkerBase.fileIO.*;
import com.checkerWeb.checker.checkerBase.sablecc.node.*;
import com.checkerWeb.checker.checkerBase.parser.*;
import com.checkerWeb.checker.checkerBase.tokenlister.*;
import com.checkerWeb.checker.checkerBase.brumby.*;

/**
 * This is the core of this program, takes as arguments (args) a pathname to a folder
 * containing java, cpp, or ada files, the file type being processed (".java", ".cpp", or ".ada") and
 * the minimum similarity percentage specified by the user.
 */

public class Application {
	
	/* Takes in the file information via configuration and file io. */
	public static void run(String[] args, int percent, String output_location) {
		ArrayList<String> directory_contents;
		ArrayList<TokenizedMethod> methods = new ArrayList<TokenizedMethod>();
		ArrayList<TokenizedMethod> similarMethods = new ArrayList<TokenizedMethod>();
		ArrayList<Token> file_tokens, method_tokens;

		String current_file;
		String qualified_name;
		String fileCompared = " ";
		String methodCompared = " ";
	
		Renamer rename;

		double perc = 0.0;
		int[] method_indices = new int[2];		
		int totalFiles = 0;
		int filesAffected = 0;
		int methodsAffected = 0;

		// Looks in the Arguments parameters
		for (int i = 0; i < args.length - 1; i++) {
			// Searches and stores files in a directory with language extensions at the end of each file name
			directory_contents = SearchFile.searchForFile(args[i], args[args.length - 1]);
			for (String file : directory_contents) {
				totalFiles++;
				// Take file-path and store it into a String
				current_file = CFilesReader.readFile(file);
				// Clean up unused tokens in all files and store it into the ArrayList "file_tokens"
				file_tokens = Parser.sanitize(Lister.ConvertToList(current_file, args[args.length - 1])); 
																											
				fileCompared = file;

				method_indices[0] = 0;
				while (method_indices[0] != -1) {
					// Find method's start and end indexes
					method_indices = Parser.findMethod(file_tokens, method_indices[1]);

					if (method_indices[0] == -1) {
						break; // Reached end of file
					} else {
						// Remove method chunk and rename identifiers accordingly
						rename = new Renamer(Parser.subarray(file_tokens, method_indices[0], method_indices[1])); // only
																													// method
						qualified_name = file + ":  " + rename.getTokens().get(1).getText() + ":  "
								+ rename.getTokens().get(1).getLine(); // saves (filepath : method name : line number) as a string
						methodCompared = qualified_name;
						rename.parseFile();
						method_tokens = rename.getTokens(); // tokens with id assignments

						for (TokenizedMethod method : methods) {

							// compute similarity percent
							perc = Parser.similarity(method.getTokens(), method_tokens);

							// If similarity percentage is equal or more than the specified close match, store it
							if (perc >= percent) {
								
								if (fileCompared.equals(file)) {
									filesAffected++;
									fileCompared = " ";
								}
								if (methodCompared.equals(qualified_name)) {
									if(methodsAffected == 0) {
										methodsAffected = 2;
									}
									else {
										methodsAffected++;
									}
									methodCompared = " ";
								}
								similarMethods.add(new TokenizedMethod(qualified_name, method.getLocation(), perc));
							}
						}
						// Add to list of unique methods to keep checking
						methods.add(new TokenizedMethod(qualified_name, method_tokens));
					}
				}
			}
		}
		
		// All directories processed and all methods added
		StringBuilder duplicatePaths = new StringBuilder();
		duplicatePaths.append("\nDuplicate found in the following files:\n");
		for (TokenizedMethod method : similarMethods) {
			duplicatePaths.append("\n").append(method.toString()).append("\n");
		}

		// Write text file with the comparison report
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(output_location);
		} catch (IOException e) {
			e.printStackTrace();
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		Date date = new Date(System.currentTimeMillis());

		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("BRUMBY CODE CHECKER");
		printWriter.print("\n Time stamp: " + formatter.format(date));
		printWriter.print("\n Percent minimum: " + percent);
		printWriter.print("\n File extension: " + args[1]);
		printWriter.print("\n Number of files: " + totalFiles);
		printWriter.print("\n Number of files affected: " + filesAffected);
		printWriter.print("\n Number of methods affected: " + methodsAffected + "\n");
	
		if(filesAffected == 0) {
			printWriter.print("\n No duplicates found!");
		}else {
			printWriter.print(duplicatePaths);
		}
		printWriter.close();
	}
}
