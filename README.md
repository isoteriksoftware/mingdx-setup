# mingdx-setup
The minGDX setup tool makes it easier to create minGDX projects on your Android devices. The projects are setup to work with [AIDE](https://www.android-ide.com/), so you must have it installed in order to run the projects.

# Usage
Download the latest version from [here](https://github.com/iSoterikTechnologies/mingdx-setup/releases/download/v1.0.0/mingdx-setup.apk) and install it.

## Creating a Project
Once installed, open the application. The interface is quite simple to understand.
To create a project, click on the `Create New Project` button. A `New Project Wizard` dialog appears. Fill in the details of your project:
- **Project Name:** The name of your project.
- **Game Class:** The name of the game class that will be generated (this is usually the project name without spaces, so the tool tries to help you fill it automatically but you can change the value).
- **Package:** The package (usually a reversed domain). This should be unique for every project.
- **Minimum SDK:** The minimum Android SDK that this project will support. Can be changed manually later.
- **Target SDK:** The target Android SDK. Can be changed manually later.

After filling the fields, click `Create Project` to create the project. You should see a message telling you that the project was created. The message tells you where the project was created at. **Due to the recent changes in Android 11, the setup can't access all of the external storage, so the tool creates projects inside the external storage allocated to it.**

Depending on your device, the storage path may differ. However, the path is usually similar to `/storage/emulated/0/Android/data/com.isoterik.mgdx.setup/files`.

## Importing a Project into AIDE
As of the time of this writing, when you install AIDE, it creates a `AppProjects` directory in your storage. While AIDE can work with projects inside other directories, it is usually a good idea to stick to the default directory.

To import a minGDX project, navigate to the storage location (like `/storage/emulated/0/Android/data/com.isoterik.mgdx.setup/files`). Copy the project you want to import into another directory like the `AppProjects` directory. 

Now open AIDE. With AIDE, open the project you just copied. minGDX projects have two main sub-directories (modules): `gdx-game` and `gdx-game-android`.
`gdx-game` is where your game codes reside and `gdx-game-android` is where android-specific codes reside. Open the `gdx-game-android` directory and you should a clickable text labeled **Open this Android Project**. Click it and AIDE should open the project. Now click the large **Play Icon** at the top of the editor to run the project. AIDE should compile the project and install the Android application generated. Open it and you should a red display slowly fading in!

## Upgrading a Project
We release a new setup tool for every version of minGDX released. The version of minGDX that comes with the setup tool is the version that will be added to any created project.
You may have installed a newer version of the setup tool and you have projects that you want to upgrade. The setup tool can upgrade your project automatically!

It's very easy. First, you must make sure there is a copy of the project inside the storage location (like `/storage/emulated/0/Android/data/com.isoterik.mgdx.setup/files`). If it is not there, simply copy it there, when you're done, you copy it back.

Now open the setup tool. All projects inside the storage location will be displayed in the `MinGdx Projects` dialog located at the right side of the screen (if you can't see them, click the `Refresh` button to reload the projects). 
Click on the project you want to upgrade and a dialog should appear. On the dialog, click the `Upgrade Project` button to upgrade your project. That's all!
