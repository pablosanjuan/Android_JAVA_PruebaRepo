package com.enfiv.notitv;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

public class SplasDesarrollador extends ActionBarActivity {

	private static final long SPLASH_SCREEN_DELAY = 6000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splas_desarrollador);
 
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent irsplash = new Intent().setClass(SplasDesarrollador.this, Identificacion.class);
                startActivity(irsplash);
                finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
}
