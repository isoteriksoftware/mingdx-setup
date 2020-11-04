package com.isoterik.mgdx.setup;

import com.badlogic.gdx.files.*;
import java.io.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.*;

public final class ProjectManager
{
	private static ProjectManager instance;

	private ProjectManager ()
	{}

	public static ProjectManager instance ()
	{
		if (instance == null)
			instance = new ProjectManager();

		return instance;
	}

	public void createProject (String projectName, String packageName,
							   String gameClassName, int minSdk, int targetSdk)
	throws ProjectError
	{
		String[] packageDirs = packageName.trim().replace('.', '\n').split("\n");
		//if (packageDirs.length < 2)
		//throw new ProjectCreationError("Package must atleast have one period(.)");

		String separator = "/";

	    FileHandle projectFile = Gdx.files.external(Constants.OUTPUT_DIR + separator + projectName);
		if (projectFile.exists())
			throw new ProjectError("A Project with this name exists!");
		else
			projectFile.mkdirs();

		createGdxDir(projectFile, packageDirs, packageName, gameClassName);
		createAndroidDir(projectFile, packageDirs, projectName, packageName, gameClassName,
						 minSdk, targetSdk);
	}

	private void createPackageDirs (String[] packageDirs, String parentDir)
	throws ProjectError
    {
		int size = packageDirs.length;
		String sep = "/";

		if (size >= 3)
		{
		    if (packageDirs[0].equals(Constants.FIRST_MGDX_PACKAGE)
				&& packageDirs[1].equals(Constants.SECOND_MGDX_PACKAGE)
				&& packageDirs[2].equals(Constants.THIRD_MGDX_PACKAGE))
				throw new ProjectError("Package name is too similar to that of minGdx");

		}

		FileHandle fh;
		parentDir = sep + parentDir;
		for (String dir : packageDirs)
		{
			parentDir += sep + dir;
			fh = Gdx.files.external(parentDir);
			if (!fh.exists())
				fh.mkdirs();
		}
	}

	private void createAndroidDir (FileHandle projectFile, String[] packageDirs,
								   String projectName, String packageName, String gameClassName, int minSdk, int targetSdk)
	throws ProjectError
	{
		String separator = "/";

		// copy android files
		FileHandle androidFiles = Gdx.files.internal(Constants.PROJECT_ANDROID_FILES_DIR);
		if (!androidFiles.exists())
			throw new ProjectError("Internal Error: Android Files Are Missing!");
	    else
		    androidFiles.copyTo(projectFile);

		// The assets dir is currently missed after the copy. This is probably because it contains no file!
		// create the assets dir manually
		FileHandle assetsDir = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game-android/assets");
		if (!assetsDir.exists())
			assetsDir.mkdirs();

		// rename hidden files
        FileHandle fh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game-android/classpath");
		FileHandle hiddenFh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game-android/.classpath");

		if (!fh.exists())
			throw new ProjectError("Internal Error: Android classpath File is Missing!");
	    else
		{
			hiddenFh.write(fh.read(), false);
			fh.delete();
		}

		fh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game-android/project");
		hiddenFh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game-android/.project");

		if (!fh.exists())
			throw new ProjectError("Internal Error: Android project File is Missing!");
	    else
		{
			hiddenFh.write(fh.read(), false);
			fh.delete();
		}

		// create project package folders
		createPackageDirs(packageDirs, projectFile.parent().name() + separator + projectFile.name() + "/gdx-game-android/src");

		// create and write missing files (MainActivity, strings, AndroidManifest)
		String pkgDir = "";
		for (String dir : packageDirs)
		    pkgDir += separator + dir;

		String androidDirPath = projectFile.parent().name() + separator + projectFile.name() + "/gdx-game-android";
		FileHandle gameDirFh = Gdx.files.external(androidDirPath);
		if (!gameDirFh.exists())
			throw new ProjectError("Internal Error: Game Android Package Folders are Missing!");
	    else
		{
			// read strings contents from internal storage
			// replace the placeholders
			String stringsFileData = Gdx.files.internal(Constants.PROJECT_STRINGS_FILE).readString();
			stringsFileData = stringsFileData.replace(Constants.PROJECT_NAME_PLACEHOLDER, projectName);

			// create the sring file and write to it
			Gdx.files.external(androidDirPath + "/res/values" + separator + "strings.xml").writeString(stringsFileData ,false);

			// read MainActivity class contents from internal storage
			// replace placeholders
			String mainClassData = Gdx.files.internal(Constants.PROJECT_MAIN_ACTIVITY_CLASS_FILE).readString();
			mainClassData = mainClassData.replace(Constants.PACKAGE_PLACEHOLDER, packageName);
			mainClassData = mainClassData.replace(Constants.GAME_CLASS_PLACEHOLDER, gameClassName);

			// create and write to the MainActivity clas
			Gdx.files.external(androidDirPath + separator + "src/" + pkgDir + separator + "MainActivity.java").writeString(mainClassData ,false);

			// read AndroidManifest file contents from internal storage
			// replace attributes
			String manifestData = Gdx.files.internal(Constants.PROJECT_MANIFEST_FILE).readString();
			manifestData = manifestData.replace(Constants.PACKAGE_PLACEHOLDER, packageName);
			manifestData = manifestData.replace(Constants.MIN_SDK_PLACEHOLDER, String.valueOf(minSdk));
			manifestData = manifestData.replace(Constants.TARGET_SDK_PLACEHOLDER, String.valueOf(targetSdk));

			// create and write to Manifest file
			FileHandle manifesFile = Gdx.files.external(androidDirPath + separator + "AndroidManifest.xml");
			manifesFile.writeString(manifestData, false);
		}
	}

	private void createGdxDir (FileHandle projectFile, String[] packageDirs,
							   String packageName, String gameClassName)
	throws ProjectError
	{
		String separator = "/";

		// copy gdx files
		FileHandle gdxFiles = Gdx.files.internal(Constants.PROJECT_GDX_FILES_DIR);
		if (!gdxFiles.exists())
			throw new ProjectError("Internal Error: GDX Files Are Missing!");
	    else
		    gdxFiles.copyTo(projectFile);

		// rename hidden filk
        FileHandle fh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game/classpath");
		FileHandle hiddenFh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game/.classpath");

		if (!fh.exists())
			throw new ProjectError("Internal Error: Gdx classpath File is Missing!");
	    else
		{
			hiddenFh.write(fh.read(), false);
			fh.delete();
		}

		fh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game/project");
		hiddenFh = Gdx.files.external(projectFile.parent().name() + separator + projectFile.name() + "/gdx-game/.project");

		if (!fh.exists())
			throw new ProjectError("Internal Error: Gdx project File is Missing!");
	    else
		{
			hiddenFh.write(fh.read(), false);
			fh.delete();
		}

		// create project package folders
		createPackageDirs(packageDirs, projectFile.parent().name() + separator + projectFile.name() + "/gdx-game/src");

		// create and write game class
		String pkgDir = "";
		for (String dir : packageDirs)
		    pkgDir += separator + dir;

		String gameDirPath = projectFile.parent().name() + separator + projectFile.name() + "/gdx-game/src" +
		    separator + pkgDir;
		FileHandle gameDirFh = Gdx.files.external(gameDirPath);
		if (!gameDirFh.exists())
			throw new ProjectError("Internal Error: Game Package Folders are Missing!");
	    else
		{
			// read the game class contents from internal storage
			// replace the placeholders
			String gameClassData = Gdx.files.internal(Constants.PROJECT_GAME_CLASS_FILE).readString();
			gameClassData = gameClassData.replace(Constants.PACKAGE_PLACEHOLDER, packageName);
			gameClassData = gameClassData.replace(Constants.GAME_CLASS_PLACEHOLDER, gameClassName);

			// create the game class and write to it
			Gdx.files.external(gameDirPath + separator + gameClassName + ".java").writeString(gameClassData ,false);
		}

	}

	public void upgradeProject(FileHandle project)
	throws ProjectError
	{
		if (!project.isDirectory())
			throw new ProjectError("This file is not a valid MinGdx peoject folder!");

		String separator = "/";

		FileHandle libsDir = Gdx.files.external(project.path() + separator + "gdx-game/libs");
		if (!libsDir.exists())
			throw new ProjectError("This project has no libs folder!");

		FileHandle mgdxFile = Gdx.files.external(libsDir.path() + "/" + Constants.MINGDX_JAR_FILE_NAME);
		if (mgdxFile.exists())
			mgdxFile.delete();

		FileHandle internalMgdxFile = Gdx.files.internal(Constants.PROJECT_GDX_FILES_DIR
														 + "/libs/" + Constants.MINGDX_JAR_FILE_NAME);

		internalMgdxFile.copyTo(libsDir);
	}

	public static class ProjectError extends IOException
	{
		public ProjectError (String message)
		{ super(message); }
	}
}
