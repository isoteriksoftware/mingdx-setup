package com.isoterik.mgdx.setup.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.isoterik.mgdx.MinGdx;
import com.isoterik.mgdx.Scene;
import com.isoterik.mgdx.Version;
import com.isoterik.mgdx.setup.Constants;
import com.isoterik.mgdx.setup.ProjectManager;
import com.isoterik.mgdx.setup.Utils;
import com.isoterik.mgdx.setup.converter.AtlasConverter;
import com.isoterik.mgdx.setup.converter.AtlasParams;
import com.isoterik.mgdx.setup.converter.XmlParams;
import com.isoterik.mgdx.ui.ActorAnimation;

import java.util.Arrays;

public class MainScene extends Scene {
	private final Skin skin;

	private final float screenW;
	private final float screenH;

	private final ActorAnimation actorAnimation;

	private Table ui;
	private List<String> fileList;
	private final ArrayMap<String, FileHandle> gameFiles;

	public MainScene() {
		setStackable(false);
		
		gameFiles = new ArrayMap<>();

		screenW = mainCamera.getWorldUnits().getScreenWidth();
		screenH = mainCamera.getWorldUnits().getScreenHeight();

		actorAnimation = ActorAnimation.instance();
		actorAnimation.setup(screenW, screenH);

		setBackgroundColor(new Color(.1f, .1f, .1f, 1f));

		skin = MinGdx.instance().assets.getSkin(Constants.SKIN_PATH);

		reloadUI();

		inputManager.setCatchBackKey(true);
		inputManager.addOnBackpressListener((mappingName, evt) -> showConfirmationDialog("Confirm Exit","Do you really want to exit the application now?",
				() -> MinGdx.instance().exit()));
	}

	private void reloadUI() {
		if (ui != null)
			ui.remove();

		ui = buildUI();
		canvas.addActor(ui);
	}

	private Table buildUI() {
		Table root = new Table();
		root.setFillParent(true);

		root.row();
		root.add(buildLeftCol()).expand().fill();
		root.add(buildRightCol()).expand().fill().padTop(30);

		return root;
	}

	private Table buildLeftCol() {
		Table tbl = new Table();

		float btnW = 500;
		float btnH = 100;
		float padBottom = 30;

		TextButton btnNewProject = new TextButton("Create New Project", skin);
		btnNewProject.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor actor)
			{
				showNewProjectDialog();
			}
		});

		TextButton btnConvertAtlas = new TextButton("Convert XML Atlas", skin);
		btnConvertAtlas.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor actor)
			{
				Window dialog = buildConversionDialog();
				centerOnScreen(dialog);
				actorAnimation.grow(dialog, .5f, Interpolation.pow5Out);
				canvas.addActor(dialog);
			}
		});

		Label versionLbl = new Label("MinGdx " + Version.VERSION, skin, "link-label");
		versionLbl.setFontScale(.9f);

		Table vtbl = new Table();
		vtbl.left().bottom().row();
		vtbl.add(versionLbl).padLeft(20);

		tbl.padTop(150);
		tbl.row();
		tbl.add(btnNewProject).size(btnW, btnH).padBottom(padBottom);
		tbl.row();
		tbl.add(btnConvertAtlas).size(btnW, btnH).padBottom(padBottom);
		tbl.row();
		tbl.add(vtbl).expand().fill();

		return tbl;
	}

	private Table buildRightCol() {
		Window window = new Window("MinGdx Projects", skin);
		window.getTitleLabel().setAlignment(Align.center);
		window.getTitleLabel().setColor(Color.GRAY);
		window.setMovable(false);

		fileList = new List<String>(skin);
		reloadGameFiles();

		TextButton btnRefresh = new TextButton("Refresh", skin);

		btnRefresh.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				reloadUI();
			}
		});

		fileList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				String selected = fileList.getSelected();
				if (selected.equals("..."))
					return;

				FileHandle file = gameFiles.get(selected);
				if (file.isDirectory())
					showProjectOptions(file);
			}
		});

		ScrollPane pane = new ScrollPane(fileList, skin);
		pane.setOverscroll(false, false);
		pane.setSmoothScrolling(true);
		pane.setScrollingDisabled(true, false);
		pane.setFadeScrollBars(false);

		window.top();
		window.row().padTop(50);
		window.add(btnRefresh).expandX().fillX().padBottom(50);
		window.row();
		window.add(pane).expand().fill();

		return window;
	}

	private Window buildNewProjectDialog() {
		final Window window = new Window("New Project Wizard", skin, "dialog");
		window.getTitleLabel().setAlignment(Align.center);
		window.getTitleLabel().setColor(Color.GRAY);

		Label projectName = new Label("Project Name", skin);
		Label gameClassName = new Label("Game Class", skin);
		Label packageName = new Label("Package", skin);
		Label minSdk = new Label("Minimum SDK", skin);
		Label targetSdk = new Label("Target SDK", skin);

		final TextField fieldProjectName = new TextField("", skin);
		fieldProjectName.setMessageText("My MinGdx Game");
		final TextField fieldGameClass = new TextField("", skin);
		fieldGameClass.setMessageText("MyMinGdxGame");
		final TextField fieldPackage = new TextField("", skin);
		fieldPackage.setMessageText("com.mycompany.mymingdxgame");
		final TextField fieldMinSdk = new TextField("14", skin);
		final TextField fieldTargetSdk = new TextField("27", skin);

		TextButton btnCancel = new TextButton("Cancel", skin);
		TextButton btnCreate = new TextButton("Create Project", skin);

		fieldProjectName.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				String text = fieldProjectName.getText().trim();
				text = text.replaceAll("\\s+", "");
				fieldGameClass.setText(text);
			}
		});

		fieldMinSdk.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				String text = fieldMinSdk.getText().trim();
				if (text.isEmpty())
					return;

				if (!Utils.isInt(text))
					evt.cancel();
			}
		});

		fieldTargetSdk.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				String text = fieldTargetSdk.getText().trim();
				if (text.isEmpty())
					return;

				if (!Utils.isInt(text))
					evt.cancel();
			}
		});

		btnCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				actorAnimation.shrinkThenRemove(window,
												.3f, Interpolation.linear);
			}
		});

		btnCreate.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a) {
				final String proj = fieldProjectName.getText().trim();
				if (proj.isEmpty()) {
					showErrorDialog("Please enter the project name");
					return;
				}

				String gameCl = fieldGameClass.getText().trim();
				final String gameClass = gameCl.replaceAll("\\s+", "");
				if (gameClass.isEmpty()) {
					showErrorDialog("Please enter the game class");
					return;
				}

				final String pkg = fieldPackage.getText().trim();
				if (pkg.isEmpty()) {
					showErrorDialog("Please enter the package");
					return;
				}

				final String msdk = fieldMinSdk.getText().trim();
				if (msdk.isEmpty()) {
					showErrorDialog("Please enter the minimum SDK");
					return;
				}

				final String tsdk = fieldTargetSdk.getText().trim();
				if (tsdk.isEmpty()) {
					showErrorDialog("Please enter the target SDK");
					return;
				}

				try {
					ProjectManager.instance().createProject(proj, pkg,
															gameClass, Math.abs(Utils.toInt(msdk)), Math.abs(Utils.toInt(tsdk)));

					showMessageDialog("Success!", "Project created in " +
						"'" + Gdx.files.external(Constants.APP_DIR).file().getAbsolutePath() + "'",
							() -> reloadUI());
					closeDialog(window);
				} catch(Exception e) {
					showErrorDialog(e.getMessage());
				}
			}
		});

		float pr = 30;
		float pt = 50;

		Table tbl = new Table();
		tbl.pad(20);
		tbl.row();
		tbl.add(projectName).left().padRight(pr);
		tbl.add(fieldProjectName).expandX().fillX();
		tbl.row().padTop(pt);
		tbl.add(gameClassName).left().padRight(pr);
		tbl.add(fieldGameClass).expandX().fillX();
		tbl.row().padTop(pt);
		tbl.add(packageName).left().padRight(pr);
		tbl.add(fieldPackage).expandX().fillX();
		tbl.row().padTop(pt);
		tbl.add(minSdk).left().padRight(pr);
		tbl.add(fieldMinSdk).expandX().fillX();
		tbl.row().padTop(pt);
		tbl.add(targetSdk).left().padRight(pr);
		tbl.add(fieldTargetSdk).expandX().fillX();

		ScrollPane pane = new ScrollPane(tbl, skin, "list");
		pane.setOverscroll(false, false);
		pane.setSmoothScrolling(true);
		pane.setScrollingDisabled(true, false);
		pane.setFadeScrollBars(false);

		window.row();
		window.add(pane).colspan(2).expandX().fillX().padTop(70);
		window.row().pad(50, 10, 10, 10);
		window.add(btnCancel).left().expandX();
		window.add(btnCreate).right();

		window.setSize(800, 600);
		window.setModal(true);
		window.setKeepWithinStage(false);
		window.setMovable(false);
		return window;
	}

	private Window buildConversionDialog () {
		final Window dialog = new Window("XML Converter", skin, "dialog");
		dialog.getTitleLabel().setAlignment(Align.center);
		dialog.getTitleLabel().setColor(Color.GRAY);

		final TextField fieldXmlName = new TextField("", skin);
		fieldXmlName.setMessageText("spritesheet.xml");
		final TextField fieldAtlasName = new TextField("", skin);
		fieldAtlasName.setMessageText("spritesheet.atlas");
		final TextField fieldAtlasW = new TextField("", skin);
		fieldAtlasW.setMessageText("1024");
		final TextField fieldAtlasH = new TextField("", skin);
		fieldAtlasH.setMessageText("1024");
		final TextField fieldParentTag = new TextField("TextureAtlas", skin);
		final TextField fieldSourceImgAttr = new TextField("imagePath", skin);
		final TextField fieldTextTag = new TextField("SubTexture", skin);
		final TextField fieldRegNameAttr = new TextField("name", skin);
		final TextField fieldRegXAttr = new TextField("x", skin);
		final TextField fieldRegYAttr = new TextField("y", skin);
		final TextField fieldRegWAttr = new TextField("width", skin);
		final TextField fieldRegHAttr = new TextField("height", skin);
		final TextField fieldChangeX = new TextField("0", skin);
		final TextField fieldChangeY = new TextField("0", skin);
		final TextField fieldChangeW = new TextField("0", skin);
		final TextField fieldChangeH = new TextField("0", skin);

		TextButton btnCancel = new TextButton("Cancel", skin);
		btnCancel.addListener(new ChangeListener() {
			public void changed(ChangeEvent evt, Actor actor) {
				closeDialog(dialog);
			}
		});

		TextButton btnConvert = new TextButton("Convert", skin);
		btnConvert.addListener(new ChangeListener() {
			public void changed(ChangeEvent evt, Actor actor) {
				/* validate input */
				if (Utils.emptyField(fieldXmlName) || Utils.emptyField(fieldAtlasName)
					|| Utils.emptyField(fieldAtlasW) || Utils.emptyField(fieldAtlasH)
					|| Utils.emptyField(fieldParentTag) || Utils.emptyField(fieldSourceImgAttr)
					|| Utils.emptyField(fieldTextTag) || Utils.emptyField(fieldRegNameAttr)
					|| Utils.emptyField(fieldRegXAttr) || Utils.emptyField(fieldRegYAttr)
					|| Utils.emptyField(fieldRegWAttr) || Utils.emptyField(fieldRegHAttr)
					|| Utils.emptyField(fieldChangeX) || Utils.emptyField(fieldChangeY)
					|| Utils.emptyField(fieldChangeW) || Utils.emptyField(fieldChangeH)) {
					showErrorDialog("Please fill-in all fields!");
					return;
				}

				if (!Utils.isInt(fieldAtlasW) || !Utils.isInt(fieldAtlasH)) {
					showErrorDialog("Atlas size parameters should be whole numbers!");
					return;
				}

				if (!Utils.isInt(fieldChangeX) || !Utils.isInt(fieldChangeY) ||
					!Utils.isInt(fieldChangeW) || !Utils.isInt(fieldChangeH)) {
					showErrorDialog("Size-Change fields accepts only numeric whole numbers!");
					return;
				}

				/* Populate the parameters and Start the conversion */
				try {
					// make sure input file exists
					String xmlFilePath = Constants.CONVERTER_INPUT_DIR + "/" + fieldXmlName.getText();
					if (!Gdx.files.external(xmlFilePath).exists()) {
						showErrorDialog("The selected input file does not exist. Please make sure the file is correctly moved to the /converter/input directory");
						return;
					}

					// make sure output file is not existing
					String atlasFilePath = Constants.CONVERTER_OUTPUT_DIR + "/" + fieldAtlasName.getText();
					if (Gdx.files.external(atlasFilePath).exists()) {
						showErrorDialog("A file with this name already exists! Please choose another name!");
						return;
					}

					AtlasParams atlasParams = new AtlasParams();
					atlasParams.atlasWidth = Utils.toInt(fieldAtlasW.getText());
					atlasParams.atlasHeight = Utils.toInt(fieldAtlasH.getText());
					atlasParams.xTuning = Math.abs(Utils.toInt(fieldChangeX.getText()));
					atlasParams.yTuning = Math.abs(Utils.toInt(fieldChangeY.getText()));
					atlasParams.wTuning = Math.abs(Utils.toInt(fieldChangeW.getText()));
					atlasParams.hTuning = Math.abs(Utils.toInt(fieldChangeH.getText()));

					XmlParams xmlParams = new XmlParams();
					xmlParams.atlasTag = fieldParentTag.getText();
					xmlParams.hAttr = fieldRegHAttr.getText();
					xmlParams.wAttr = fieldRegWAttr.getText();
					xmlParams.xAttr = fieldRegXAttr.getText();
					xmlParams.yAttr = fieldRegYAttr.getText();
					xmlParams.nameAttr = fieldRegNameAttr.getText();
					xmlParams.pathAttr = fieldSourceImgAttr.getText();
					xmlParams.textureTag = fieldTextTag.getText();

					AtlasConverter converter = new AtlasConverter(xmlParams, atlasParams,
																  Gdx.files.external(xmlFilePath), Gdx.files.external(atlasFilePath));
					converter.convert();
					showMessageDialog("Success!", "Atlas File created successfully!");
				}
				catch (Exception e) {
					showErrorDialog("An error occurred! Please check your input and try again.\n\n" + e.getMessage());
				}
			}
		});

		String labelType = "default";
		float pad = 25;
		float fieldH = 35;

		Table tbl = new Table();

		tbl.top();
		tbl.add(new Label("XML File Name: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldXmlName).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Atlas File Name: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldAtlasName).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Atlas Width: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldAtlasW).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Atlas Height: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldAtlasH).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Root Tag: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldParentTag).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Source Image Attribute: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldSourceImgAttr).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Texture Tag: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldTextTag).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Region Name Attribute: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldRegNameAttr).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Region -X Attribute: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldRegXAttr).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Region -Y Attribute: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldRegYAttr).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Region Width Attribute: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldRegWAttr).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Region Height Attribute: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldRegHAttr).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Delta X: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldChangeX).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Delta Y: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldChangeY).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Delta Width: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldChangeW).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();
		tbl.add(new Label("Delta Height: ", skin, labelType)).left().pad(pad).padRight(10);
		tbl.add(fieldChangeH).expandX().fillX().pad(pad).height(fieldH);
		tbl.row();

		ScrollPane scroll = new ScrollPane(tbl, skin, "list");
		scroll.setFadeScrollBars(false);
		scroll.setSmoothScrolling(true);

		Table btnsTbl = new Table();
		btnsTbl.left();
		btnsTbl.add(btnCancel).pad(20).padRight(60);
	    btnsTbl.add(btnConvert).pad(20);

		dialog.top();
		dialog.row().padTop(60);
	    dialog.add(scroll).expandX().fillX().padBottom(15);
	    dialog.row();
	    dialog.add(btnsTbl).expandX().fillX();

		dialog.setSize(900, 600);
		dialog.setModal(true);
		dialog.setKeepWithinStage(false);
		dialog.setMovable(false);
		return dialog;
	}

	private void showProjectOptions(final FileHandle file) {
		final Window window = new Window(file.name(), skin, "dialog");
		window.getTitleLabel().setAlignment(Align.center);
		window.getTitleLabel().setColor(Color.GRAY);

		TextButton btnDelete = new TextButton("Delete Project", skin);
		TextButton btnUpgrade = new TextButton("Upgrade Project", skin);
		TextButton btnClose = new TextButton("Close", skin);

		btnClose.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a) {
				closeDialog(window);
			}
		});

		btnDelete.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a) {
				closeDialog(window);

				try {
					showConfirmationDialog("Warning!", "Do you really want to delete " + file.name() + "?\n\n"
							+ "This does not affect the AppProjects folder", new Runnable() {
						@Override
						public void run() {
							file.deleteDirectory();
							reloadUI();
						}
					});
				} catch(Exception e) {
					showErrorDialog(e.getMessage());
				}
			}
		});

		btnUpgrade.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a) {
				closeDialog(window);

				try {
					ProjectManager.instance().upgradeProject(file);

					showMessageDialog("Success!", "Your project was upgraded successfully!");
				} catch(Exception e) {
					showErrorDialog(e.getMessage());
				}
			}
		});

		float btnH = 50;

		window.row().padTop(60);
		window.add(btnUpgrade).expandX().fillX().height(btnH);
		window.row().padTop(30);
		window.add(btnDelete).expandX().fillX().expandY().height(btnH);
		window.row().padTop(100);
		window.add(btnClose).left().expandX();

		window.pack();
		window.setWidth(500);
		window.setModal(true);
		window.setKeepWithinStage(false);
		window.setMovable(false);

		centerOnScreen(window);
		actorAnimation.slideIn(window, ActorAnimation.RIGHT, .5f);
		canvas.addActor(window);
	}

	private void showNewProjectDialog() {
		Window dialog = buildNewProjectDialog();
		centerOnScreen(dialog);

		actorAnimation.grow(dialog, .5f, Interpolation.pow5Out);
		canvas.addActor(dialog);
	}

	private void showMessageDialog(String title, String message, final Runnable onClose) {
		final Window dialog = new Window(title, skin, "dialog");
		dialog.getTitleLabel().setColor(Color.GRAY);
		dialog.getTitleLabel().setAlignment(Align.center);

		dialog.setModal(true);
		dialog.setKeepWithinStage(false);

		Label msg = new Label(message, skin);
		msg.setWrap(true);

		TextButton btnOk= new TextButton("Okay", skin);

		btnOk.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				actorAnimation.shrinkThenRemove(dialog,
												.1f, Interpolation.linear);

				if (onClose != null) {
					  onClose.run();
				}
			}
		});

		dialog.row().pad(0, 10, 10, 10);
		dialog.add(msg).expand().fill();
		dialog.row();
		dialog.add(btnOk).pad(20).left().expandX();

		dialog.setSize(500, 300);
		centerOnScreen(dialog);

		actorAnimation.slideIn(dialog, ActorAnimation.UP,
								   .5f);
		canvas.addActor(dialog);
	}

	private void showMessageDialog(String title, String message) {
		showMessageDialog(title, message, null);
	}
	
	private void showErrorDialog(String message) {
		showMessageDialog("Oops!", message);
	}

	private void showConfirmationDialog(String title, String message,
										final Runnable onConfirm) {
		final Window dialog = new Window(title, skin, "dialog");
		dialog.getTitleLabel().setColor(Color.GRAY);
		dialog.getTitleLabel().setAlignment(Align.center);

		dialog.setModal(true);
		dialog.setKeepWithinStage(false);

		Label msg = new Label(message, skin);
		msg.setWrap(true);

		TextButton btnCancel = new TextButton("Cancel", skin);
		TextButton btnYes = new TextButton("Yes Please", skin);

		btnCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				closeDialog(dialog);
			}
		});

		btnYes.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent evt, Actor a)
			{
				closeDialog(dialog);

				if (onConfirm != null)
					onConfirm.run();
			}
		});

		dialog.row().pad(40, 10, 10, 10);
		dialog.add(msg).expand().fill().colspan(2);
		dialog.row();
		dialog.add(btnCancel).pad(90, 20, 20, 20).left().expandX();
		dialog.add(btnYes).pad(90, 20, 20, 20).right();

		dialog.pack();
		dialog.setWidth(700);
		centerOnScreen(dialog);

		actorAnimation.grow(dialog, .5f, Interpolation.bounceOut);
		canvas.addActor(dialog);
	}

	private void centerOnScreen(Actor actor) {
		actor.setX((screenW - actor.getWidth())/2f);
		actor.setY((screenH - actor.getHeight())/2f);
		actor.setOrigin(Align.center);
	}

	private void closeDialog(Window dialog, float duration) {
		actorAnimation.shrinkThenRemove(dialog,
										duration,  Interpolation.pow5Out);
	}

	private void closeDialog(Window dialog) {
		closeDialog(dialog, .5f);
	}

	private void reloadGameFiles() {
		gameFiles.clear();	

		FileHandle appDir = Gdx.files.external(Constants.APP_DIR);
		if (!appDir.exists())
			return;

		FileHandle[] files = appDir.list();
		Arrays.sort(files, (f1, f2) -> f1.extension().compareTo(f2.extension()));

		gameFiles.put("...", null);
		for (FileHandle fh : files) {
			gameFiles.put(fh.name(), fh);
		}

		fileList.setItems(gameFiles.keys().toArray());
	}
}