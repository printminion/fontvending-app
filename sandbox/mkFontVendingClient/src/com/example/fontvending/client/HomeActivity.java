package com.example.fontvending.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.fontvending.client.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private Spinner spinner1;
	private EditText editText1;
	private Button button1, button2;
	private CheckBox checkBox1, checkBox2;

	private FontArrayAdapter<String> adapterFonts;
	private List<String> mFontsList = new ArrayList<String>();

	private FontView mFontView;
	private String mCurrentFontPath;
	private String mCurrentFontFolder;

	protected static final String FONTVENDING_PACKAGE_NAME = "com.kupriyanov.fontvending";

	protected static final int REQUEST_NEW_FONTS = 1;

	public static final int RESULT_VENDING_FONT_INSTALLED = 1;
	public static final int RESULT_VENDING_FONT_UNINSTALLED = 2;
	public static final int RESULT_VENDING_FONT_INSTALLATION_NONE = 3;

	private static final String FONTCLIENT_PACKAGE_NAME = "com.example.fontvending.client";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/*
		 * disable screen rotation
		 */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
		checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
		editText1 = (EditText) findViewById(R.id.editText1);
		mFontView = (FontView) findViewById(R.id.lblExample);
		spinner1 = (Spinner) findViewById(R.id.spinner1);

		editText1.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_UP) {
					updatePreview();
				}

				return false;
			}
		});

		selectCheckbox(R.id.checkBox1);
		populateSpinner();

	}

	private void populateSpinner() {

		mFontsList = readAllFonts(mCurrentFontFolder);

		adapterFonts = new FontArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mFontsList);
		adapterFonts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner1.setPrompt("Choose font");
		spinner1.setAdapter(adapterFonts);
		spinner1.setOnItemSelectedListener(spnFontOnItemSelectedListener);

	}

	public void onChangeFontsLocation(View v) {

		selectCheckbox(v.getId());
		populateSpinner();

	}

	private void selectCheckbox(int id) {

		if (id == R.id.checkBox1) {
			mCurrentFontFolder = Environment.getExternalStorageDirectory() + "/fonts/";

			button1.setEnabled(true);
			button2.setEnabled(false);
			checkBox1.setChecked(true);
			checkBox2.setChecked(false);

		} else {
			mCurrentFontFolder = Environment.getExternalStorageDirectory() + "/fonts_custom/";

			button1.setEnabled(false);
			button2.setEnabled(true);
			checkBox1.setChecked(false);
			checkBox2.setChecked(true);

		}

	}

	public void onAddFontsToDefaultLocation(View v) {

		/*
		 * check if font vending has been installed and start it if so
		 */
		if (!ifAppExist(getApplicationContext(), FONTVENDING_PACKAGE_NAME)) {
			installPackageFromMarket(FONTVENDING_PACKAGE_NAME);
			return;
		}

		Intent intent = new Intent(android.content.Intent.ACTION_GET_CONTENT);
		intent.setPackage(FONTVENDING_PACKAGE_NAME);
		intent.setType("font/*");

		/*
		 * install into custom folder
		 */
		if (checkBox2.isChecked()) {
			intent.putExtra("CLIENT_PACKAGE", FONTCLIENT_PACKAGE_NAME);
			intent.putExtra("TARGET_PATH", mCurrentFontFolder);
		}

		startActivityForResult(intent, REQUEST_NEW_FONTS);

		return;

	}

	public void onAddFontsToCustomLocation(View v) {
		/*
		 * check if font vending has been installed and start it if so
		 */
		if (!ifAppExist(getApplicationContext(), FONTVENDING_PACKAGE_NAME)) {
			installPackageFromMarket(FONTVENDING_PACKAGE_NAME);
			return;
		}

		Intent intent = new Intent(android.content.Intent.ACTION_GET_CONTENT);
		intent.setPackage(FONTVENDING_PACKAGE_NAME);
		intent.setType("font/*");

		intent.putExtra("CLIENT_PACKAGE", FONTCLIENT_PACKAGE_NAME);
		intent.putExtra("TARGET_PATH", mCurrentFontFolder);

		startActivityForResult(intent, REQUEST_NEW_FONTS);

		return;
	}

	/**
	 * show dialog: please install font vending and start the market app
	 * 
	 * @param packageToStart
	 */
	private void installPackageFromMarket(final String packageToStart) {

		new AlertDialog.Builder(this).setIcon(R.drawable.icon)
				.setTitle(getString(R.string.dialog_install_fontsvending_title))
				.setMessage(getString(R.string.dialog_install_fontsvending))
				.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:" + packageToStart));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);

					}
				}).setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_NEW_FONTS) {

			switch (resultCode) {
			case RESULT_VENDING_FONT_INSTALLED:
				Toast.makeText(getApplicationContext(), "some fonts are installed", Toast.LENGTH_SHORT).show();
				populateSpinner();

				break;
			case RESULT_VENDING_FONT_UNINSTALLED:
				Toast.makeText(getApplicationContext(), "some fonts are removed", Toast.LENGTH_SHORT).show();

				populateSpinner();

				break;

			default:
				Toast.makeText(getApplicationContext(), "no font installed or removed", Toast.LENGTH_SHORT).show();

				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private OnItemSelectedListener spnFontOnItemSelectedListener = new OnItemSelectedListener() {

		private TextView v;
		private Typeface typeface;

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			String path = (String) parent.getItemAtPosition(position);

			try {

				v = (TextView) view;
				mCurrentFontPath = path;
				typeface = Typeface.createFromFile(new File(path));
				v.setTypeface(typeface);

				updatePreview();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	};

	private void updatePreview() {
		mFontView.setFontPreview(getApplicationContext(), mCurrentFontPath, editText1.getText().toString(), false);
	}

	/**
	 * Check if application exist
	 * 
	 * @param context
	 * @param packageName
	 * @return boolean
	 */
	public static boolean ifAppExist(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			return info != null ? true : false;
		} catch (NameNotFoundException e) {
		}

		return false;
	}

	public static List<String> readAllFonts(String path) {
		List<String> fontNames = new ArrayList<String>();

		try {
			File temp = new File(path);
			String fontSuffix = ".ttf";
			String fontName = "";
			for (File font : temp.listFiles()) {
				fontName = font.getName();
				if (fontName.endsWith(fontSuffix)) {
					fontNames.add(font.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fontNames;
	}

}