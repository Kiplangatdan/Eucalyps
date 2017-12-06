package eucalyps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eucalyps.mine.R;
import fr.uoe.eucalyps.helper.Item;

public class ItemLocatorGame extends MainActivity {
   public static final int MAX_SCORE = 10;
	private int code = -1;
	private Item item = null;
	private Item currentItem = null;
	private ImageView img;
	private TextView text;
	private Toast toast;
	protected String TAREYOUSURE;
	protected String TYES, TNO;
	protected String TINPUTCODE;
	protected String TPLEASEINPUT;
	protected String TOK, TCANCEL,TSKIP;
	protected int item_score = MAX_SCORE; 
	protected String TPLEASENUMBER;
	protected String TCORRECT;
	protected String TWRONG;
	private Resources res = null;

	// TODO add story that is shown before/after item.

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// Teurastetaan otsikkopalkki pois:
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Teurastetaan status-palkki pois (full screen):
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		final View v = getLayoutInflater().inflate(R.layout.itemlocator_normal,
				null);
		v.setKeepScreenOn(true);
		setContentView(v);
		getItem();
		setUpViews();
		makeDialogText();
		// get item from the bundle
	}

	@SuppressWarnings("unchecked")
	private void getItem() {
	    final Bundle b = getIntent().getExtras();
	    item = (Item) b.getSerializable("ITEM");
	}
	private void makeDialogText() {
		// TODO Auto-generated method stub
		res = this.getResources();
		TAREYOUSURE = res.getString(R.string.dialog_question);
		TYES = res.getString(R.string.dialog_yes);
		TNO = res.getString(R.string.dialog_no);
		TINPUTCODE = res.getString(R.string.TinputCode);
		TPLEASEINPUT = res.getString(R.string.TpleaseInputCode);
		TOK = res.getString(R.string.tag_ok);
		TCANCEL = res.getString(R.string.cancel);
		TPLEASENUMBER = res.getString(R.string.pleaseNumber);
		TCORRECT = res.getString(R.string.correctAnswer);
		TWRONG = res.getString(R.string.wrongAnswer);
		TSKIP=res.getString(R.string.skip);
	}

	private void setUpViews() {
		// TODO Auto-generated method stub
		final Button inputBtn = (Button) findViewById(R.id.itemlocator_btninput);
		inputBtn.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				showCodeInput();
			}
		});

		final Button hintBtn = (Button) findViewById(R.id.itemlocator_hint);
		hintBtn.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				showHint();
			}
		});

		img = (ImageView) findViewById(R.id.itemlocator_img);
		text = (TextView) findViewById(R.id.itemlocator_story);

		// set image, if available
		if (item.imgId != null && item.imgId.trim().length() > 0) {
			final int id = getResources().getIdentifier(
					"drawable/" + item.imgId.trim(), null, getPackageName());
			img.setImageResource(id);
			img.setAdjustViewBounds(true);
			img.setVisibility(View.VISIBLE);
			text.setText(item.description);
		} else {
			img.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected Dialog onCreateDialog(final int id) {
		AlertDialog.Builder dlgAlert;
		dlgAlert = new AlertDialog.Builder(this);
		if (id == -1) {
			dlgAlert.setMessage(TAREYOUSURE);
			dlgAlert.setPositiveButton(TYES,
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int id) {
							toast.cancel();
							item = null;
							text = null;
							if (img.getDrawable() != null)
								img.getDrawable().setCallback(null);
							img = null;
							toast = null;
							System.gc();
							setResult(-1);
							finish();
						}
					});
			dlgAlert.setNegativeButton(TNO, null);
			dlgAlert.setCancelable(false);
		}
		return dlgAlert.create();
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent msg) {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
         sound.play(click, 1, 1, 1, 0, 0.5f);
         showDialog(QUIT);
         return true;
      } 
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
         sound.play(click, 1, 1, 1, 0, 0.75f);
         return true;
      }
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
         sound.play(click, 1, 1, 1, 0, 1.25f);
         return true;
      }
      else
			return super.onKeyDown(keyCode, msg);
	}

	private void showHint() {
		// TODO: more sophisticated way of showing hints
		toast.setText("");
		toast.setText(item.hint);
		toast.show();
	}

	private void showCodeInput() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(TINPUTCODE);
		alert.setMessage(TPLEASEINPUT);

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);

		alert.setPositiveButton(TOK, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int whichButton) {
				final String value = input.getText().toString();

				try {
					code = Integer.parseInt(value);
				} catch (final NumberFormatException nfe) {
					code = -1;
				}
				codeInserted();
			}
		});

		alert.setNeutralButton(TCANCEL, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int whichButton) {
				code = -1;
			}
		});
		alert.setNegativeButton(TSKIP, new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog,
					final int whichButton) {
				code = -1;;
				setResult(0);
				 finish();
			}
		});
		alert.show();
	}

	private void codeInserted() {
		if (code == -1) {
			toast.setText("");
			toast.setText(TPLEASENUMBER);
			toast.show();
			return;
		}
		if (item.code == code) {
//			Player.POINTS+=Item.value;
			toast.setText("");
			toast.setText(TCORRECT);
			toast.show();
			setResult(this.item_score);
         finish();
		} else {
			if(item_score>4)
			item_score-=2;
			toast.setText("");
			toast.setText(TWRONG);
			toast.show();
			return;
		}
	}
}