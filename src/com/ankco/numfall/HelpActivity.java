package com.ankco.numfall;

import com.example.numfall.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends FragmentActivity {
	Button backToMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		backToMenu = (Button) findViewById(R.id.backToMenu);
		backToMenu.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent("com.example.numfall.MenuActivity");
				startActivity(intent);
			}
		});
	}
}
