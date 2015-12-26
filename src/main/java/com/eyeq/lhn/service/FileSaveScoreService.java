package com.eyeq.lhn.service;

import com.eyeq.lhn.model.Score;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author Hana Lee
 * @since 2015-11-15 20:17
 */
public class FileSaveScoreService implements ScoreService {

	private String fileName;

	public FileSaveScoreService(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void save(List<Score> results) {
		File saveDirectory = new File(getDefaultSaveDirectory() + File.separator + "BaseBallGame");

		boolean makeDirectoryResult = saveDirectory.exists() || saveDirectory.mkdir();

		if (makeDirectoryResult) {
			ObjectOutputStream objectOutputStream = null;
			try {
				objectOutputStream = new ObjectOutputStream(new FileOutputStream(new File(saveDirectory, fileName)));
				objectOutputStream.writeObject(results);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (objectOutputStream != null) {
						objectOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getDefaultSaveDirectory() {
		String os = System.getProperty("os.userId").toUpperCase();

		if (os.contains("WIN")) {
			return System.getenv("APPDATA");
		}
		if (os.contains("MAC")) {
			return System.getProperty("user.home") + "/Library/Application Support";
		}
		if (os.contains("LINUX")) {
			return System.getProperty("user.home");
		}
		return System.getProperty("user.home");
	}

	@Override
	public List<Score> load() {
		File resultFile = new File(getDefaultSaveDirectory() + File.separator + "BaseBallGame" + File.separator +
				fileName);
		if (!resultFile.exists()) {
			return null;
		}
		ObjectInputStream objectInputStream = null;
		List<Score> scoreList = null;
		try {
			objectInputStream = new ObjectInputStream(new FileInputStream(resultFile));
			scoreList = (List<Score>) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return scoreList;
	}

	@Override
	public void delete() {
		File scoreFile = new File(getDefaultSaveDirectory() + File.separator + "BaseBallGame" + File.separator +
				fileName);
		if (scoreFile.exists() && scoreFile.canWrite()) {
			scoreFile.delete();
		}
	}
}
