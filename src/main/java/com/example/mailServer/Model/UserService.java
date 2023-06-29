package com.example.mailServer.Model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class UserService {

	private static final String PATH = "./src/main/java/com/example/mailServer/file/";

	public Set<String> getUsernamesFromDirectory(String username) {
		File directory = new File(PATH + username);
		File[] files = directory.listFiles();
		Set<String> usernames = new HashSet<>();

		assert files != null;
		for (File file : files) {
			usernames.add(file.getName());
		}

		return usernames;
	}

	public Set<String> createUserFolders(String username) {
		File directory = new File(PATH + username);
		directory.mkdir(); // create also in and out folder
		File inbox = new File(PATH + username + "/in");
		inbox.mkdir();
		File outbox = new File(PATH + username + "/out");
		outbox.mkdir();

		return getUsernamesFromDirectory(username);

	}
}
