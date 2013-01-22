package jelfs.android.laptimer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.media.AudioManager;
import android.media.SoundPool;

public class MainActivity extends Activity {

	private Vibrator vibrator;
	private SoundPool soundPool;
    int soundID;
    private TextView countdown;
    private TextView startedAt;
    private TextView numberOfLapsLabel;
    private int numberOfLaps = 0;
    private EditText walk;
    private EditText run;
    private CountDownTimer timer;
    long timeLeft = 0;
    AudioManager audioManager;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);		
		soundID = soundPool.load(this, R.raw.standard_1, 1);
		countdown = (TextView) findViewById(R.id.countdown);
		startedAt = (TextView) findViewById(R.id.startedAt);
		numberOfLapsLabel = (TextView) findViewById(R.id.numberOfLaps);
		run = (EditText) findViewById(R.id.runtime);
		walk = (EditText) findViewById(R.id.walktime);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);	
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
	}
	
	private void hideKeyboardOnButtonClick(IBinder token){
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void honk(){
 	    float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
 	    float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
 	    float volume = actualVolume / maxVolume;
     	soundPool.play(soundID, volume, volume, 1, 0, 1f);
        vibrator.vibrate(500);
	}
	
	private void startRunTimer(){
		timeLeft = Long.parseLong(run.getText().toString()) * 1000;
		timer = new CountDownTimer(timeLeft, 1000) {

		    public void onTick(long millisUntilFinished) {
		    	countdown.setText("Run for: " + millisUntilFinished / 1000);
		    }
	
		    public void onFinish() {
		    	countdown.setText(R.string.blank);
		    	honk();
		    	startWalkTimer();
		    }
	    }.start();
	}
	
	private void startWalkTimer(){
		timeLeft = Long.parseLong(walk.getText().toString()) * 1000;
		timer = new CountDownTimer(timeLeft, 1000) {

		    public void onTick(long millisUntilFinished) {
		    	countdown.setText("Walk for: " + millisUntilFinished / 1000);
		    }
	
		    public void onFinish() {
		    	countdown.setText(R.string.blank);
		    	honk();
		    	numberOfLaps += 1;
		    	numberOfLapsLabel.setText(String.format("Number of laps: %d", numberOfLaps));
		    	startRunTimer();
		    }
	    }.start();
	}
	
	public void startTimer(View view) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date now = Calendar.getInstance().getTime();    
		String date = String.format("Started At: %s", df.format(now));
		startedAt.setText(date);
		numberOfLapsLabel.setText(String.format("Number of laps: %d", numberOfLaps));
		hideKeyboardOnButtonClick(run.getApplicationWindowToken());	
		startRunTimer();		
	}
	
	public void stopTimer(View view) {
		if(timer != null){
			timer.cancel();
			timer = null;
		}
		countdown.setText(R.string.zero);
		startedAt.setText("Started At:");
		numberOfLapsLabel.setText("Number of Laps:");
		numberOfLaps = 0;
	}

}
