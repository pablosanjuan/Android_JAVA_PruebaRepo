package com.enfiv.notitv;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

public class Redireccion extends ActionBarActivity{
//	private static final long SPLASH_SCREEN_DELAY = 0;
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_app);
        
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);;
		int ID = getIntent().getIntExtra("ID", 8);
		nm.cancel(ID);
        
		  Intent irsplash = new Intent().setClass(Redireccion.this, Principal.class);
	      startActivity(irsplash);
	      finish();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(task, SPLASH_SCREEN_DELAY);
//    }
}
}
