package com.isoterik.mgdx.setup;

public abstract class Constants {
    /* Dimensional Constants */
    public static final float GUI_WIDTH = 1280;
    public static final float GUI_HEIGHT = 720;

    /* File Constants */
	public static final String APPPROJECTS_DIR = "AppProjects/";
    public static final String APP_DIR = "";
    public static final String INPUT_DIR = APP_DIR;
    public static final String OUTPUT_DIR = APP_DIR;
    public static final String CONVERTER_DIR = APP_DIR;
    public static final String CONVERTER_INPUT_DIR = CONVERTER_DIR;
    public static final String CONVERTER_OUTPUT_DIR = CONVERTER_DIR;

	public static final String PROJECT_FILES_DIR = "project_template";
	public static final String PROJECT_ANDROID_FILES_DIR = PROJECT_FILES_DIR + "/gdx-game-android";
	public static final String PROJECT_GDX_FILES_DIR = PROJECT_FILES_DIR + "/gdx-game";
	public static final String PROJECT_GAME_CLASS_FILE = PROJECT_FILES_DIR + "/GameClass.java";
	public static final String PROJECT_MAIN_ACTIVITY_CLASS_FILE = PROJECT_FILES_DIR + "/MainActivity.java";
	public static final String PROJECT_MANIFEST_FILE = PROJECT_FILES_DIR + "/AndroidManifest.xml";
	public static final String PROJECT_STRINGS_FILE = PROJECT_FILES_DIR + "/strings.xml";
	public static final String MINGDX_JAR_FILE_NAME = "mingdx-mobile.jar";

    /* Placeholder Constants */
    public static final String PACKAGE_PLACEHOLDER = "_PKG_";
    public static final String GAME_CLASS_PLACEHOLDER = "_GAME_CLASS_";
    public static final String PROJECT_NAME_PLACEHOLDER = "_PROJECT_NAME_";
    public static final String MIN_SDK_PLACEHOLDER = "_MIN_SDK_";
    public static final String TARGET_SDK_PLACEHOLDER = "_TARGET_SDK_";
    public static final String FIRST_MGDX_PACKAGE = "com";
    public static final String SECOND_MGDX_PACKAGE = "isoterik";
    public static final String THIRD_MGDX_PACKAGE = "mgdx";

    /* Assets Constants */
    public static final String SKIN_PATH = "skin1/uiskin.json";
}
