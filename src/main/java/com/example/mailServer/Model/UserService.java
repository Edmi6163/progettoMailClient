package com.example.mailServer.Model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class UserService {

	private static final String PATH = "./src/main/java/com/example/mailServer/file/";

	/*
	 * @brief: using Set data structure for uniqueness of elements, avoiding the
	 * need to check each files name
	 *
	 * @return: true if the username is in the directory, false otherwise
	 *
	 * @note: the directory is hardcoded, it should be changed to a relative path
	 */
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
		directory.mkdir();
		File inbox = new File(PATH + username + "/in");
		inbox.mkdir();
		File outbox = new File(PATH + username + "/out");
		outbox.mkdir();

		return getUsernamesFromDirectory(username);

	}
}
