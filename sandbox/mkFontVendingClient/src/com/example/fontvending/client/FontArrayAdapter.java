package com.example.fontvending.client;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FontArrayAdapter<T> extends ArrayAdapter<String> {

	private TextView txtLabel;
	private Typeface typeface;
	private int minHeight;
	private int fontSize;
	private String sample;

	public FontArrayAdapter(Context context, int textViewResourceId, String[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	public FontArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		init();
	}

	public FontArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);
		init();
	}

	public FontArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
		super(context, resource, textViewResourceId, objects);
		init();
	}

	public FontArrayAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
		init();
	}

	private void init() {
		minHeight = (int) FontView.dipToPx(getContext(), 60);
		fontSize = (int) FontView.dipToPx(getContext(), 40);
	}

	public void setSampleText(String text) {
		this.sample = text;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {

		View view;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// View row = inflater.inflate(R.layout.row, parent, false);
			view = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
		} else {
			view = convertView;
		}

		try {

			final File file = new File((String) getItem(position));
			String fontName = file.getName(); //get file name only

			txtLabel = (TextView) view;
			txtLabel.setMinHeight(minHeight);

			if (this.sample != null && this.sample.length() > 0) {
				txtLabel.setText(this.sample);
			} else {
				txtLabel.setText(fontName);
			}
			txtLabel.setGravity(Gravity.CENTER_VERTICAL);
			txtLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			txtLabel.setPadding(10, 0, 0, 0);
			typeface = Typeface.createFromFile(file);
			txtLabel.setTypeface(typeface);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return view;
	}

}
