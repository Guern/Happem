package com.happem.happem;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_activity);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			TextView textView = (TextView) findViewById(R.id.outputView);
			textView.setText(extras.getString("notificationType"));
		}
	}

}
