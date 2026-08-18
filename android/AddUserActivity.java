package com.modxlab.admin;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.AndroidSketchwareMaster.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.*;
import cyberalpha.ph.particle.*;
import java.io.*;
import java.text.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class AddUserActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private String Device = "";
	private HashMap<String, Object> map = new HashMap<>();
	private String key = "";
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private LinearLayout linear28;
	private LinearLayout linear5;
	private LinearLayout linear4;
	private LinearLayout linear29;
	private Button button1;
	private TextView textview8;
	private TextView textview9;
	private LinearLayout l1;
	private LinearLayout l2;
	private ImageView imageview1;
	private EditText user;
	private EditText token;
	private LinearLayout l3;
	private LinearLayout linear30;
	private EditText valid;
	private TextView textview10;
	private ImageView imageview2;
	private RadioButton radiobutton1;
	private RadioButton radiobutton2;
	
	private DatabaseReference User = _firebase.getReference("User");
	private ChildEventListener _User_child_listener;
	private TimerTask Timer;
	private Calendar calendar = Calendar.getInstance();
	private Calendar cal = Calendar.getInstance();
	private Calendar calendar1 = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.add_user);
		initialize(_savedInstanceState);
		FirebaseApp.initializeApp(this);
		AdminApi.ensureSignedIn(null); // SECURITY: guarantee an admin session before any write
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		linear28 = findViewById(R.id.linear28);
		linear5 = findViewById(R.id.linear5);
		linear4 = findViewById(R.id.linear4);
		linear29 = findViewById(R.id.linear29);
		button1 = findViewById(R.id.button1);
		textview8 = findViewById(R.id.textview8);
		textview9 = findViewById(R.id.textview9);
		l1 = findViewById(R.id.l1);
		l2 = findViewById(R.id.l2);
		imageview1 = findViewById(R.id.imageview1);
		user = findViewById(R.id.user);
		token = findViewById(R.id.token);
		l3 = findViewById(R.id.l3);
		linear30 = findViewById(R.id.linear30);
		valid = findViewById(R.id.valid);
		textview10 = findViewById(R.id.textview10);
		imageview2 = findViewById(R.id.imageview2);
		radiobutton1 = findViewById(R.id.radiobutton1);
		radiobutton2 = findViewById(R.id.radiobutton2);
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_AddClickEffectAt(button1);
				if (user.getText().toString().equals("")) {
					((EditText)user).setError("User cannot be blank!");
				} else {
					if (token.getText().toString().equals("")) {
						((EditText)token).setError("Enter a password!");
					} else {
						if (valid.getText().toString().equals("")) {
							((EditText)valid).setError("Set expiry date!");
						} else {
							if (Device.equals("")) {
								SketchwareUtil.showMessage(getApplicationContext(), "Set the number of devices!");
							} else {
								SketchwareUtil.hideKeyboard(getApplicationContext());
								_ProgresbarShow("Key Generating...");
								// SECURITY: key generation + database write now happen inside
								// the "addUser" Cloud Function (Admin SDK, server-side only).
								AdminApi.addUser(user.getText().toString(), token.getText().toString(), Device, valid.getText().toString(), new AdminApi.Callback() {
									@Override
									public void onSuccess(HashMap<String, Object> data) {
										Timer = new TimerTask() {
											@Override
											public void run() {
												runOnUiThread(new Runnable() {
													@Override
													public void run() {
														_ProgresbarDimiss();
														finish();
													}
												});
											}
										};
										_timer.schedule(Timer, (int)(1000));
									}
									@Override
									public void onError(String message) {
										_ProgresbarDimiss();
										SketchwareUtil.showMessage(getApplicationContext(), "Failed: " + message);
									}
								});
							}
						}
					}
				}
			}
		});
		
		linear30.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				{PopupMenu popup = new PopupMenu(AddUserActivity.this, linear30);
					android.view.Menu menu = popup.getMenu();
					
					menu.add("24 Hours");
					menu.add("7 Days");
					menu.add("15 Days");
					menu.add("30 Days");
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						
						public boolean onMenuItemClick(android.view.MenuItem item) {
							switch (item.getTitle().toString()) {
								
								case "24 Hours":
								valid.setText("1");
								textview10.setText("24 Hours");
								return true;
								case "7 Days":
								valid.setText("7");
								textview10.setText("7 Days");
								return true;
								case "15 Days":
								valid.setText("15");
								textview10.setText("15 Days");
								return true;
								case "30 Days":
								valid.setText("30");
								textview10.setText("30 Days");
								return true;
								
								default: return false;
							}
						}
					});
					
					
					popup.show();}
			}
		});
		
		radiobutton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				if (_isChecked) {
					Device = "1";
					radiobutton1.setChecked(true);
					radiobutton2.setChecked(false);
				}
			}
		});
		
		radiobutton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton _param1, boolean _param2) {
				final boolean _isChecked = _param2;
				if (_isChecked) {
					Device = "∞";
					radiobutton1.setChecked(false);
					radiobutton2.setChecked(true);
				}
			}
		});
		
		_User_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {
				
			}
			
			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		User.addChildEventListener(_User_child_listener);
	}
	
	private void initializeLogic() {
		_dialogTheme();
		linear1.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)15, 0xFFFFFFFF));
		((LinearLayout)l1).removeView(user); 
		final com.google.android.material.textfield.TextInputLayout e = new com.google.android.material.textfield.TextInputLayout (this);
		e.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)); 
		
		e.setPadding((int)getDip(2), (int)getDip(2) , (int)getDip(2) , (int)getDip(2) );
		e.setGravity(Gravity.CENTER);
		e.setHintEnabled(true);
		e.setHint("Username");
		e.setBoxBackgroundMode(e.BOX_BACKGROUND_OUTLINE); 
		e.setBoxStrokeColor(0xFF070707);
		e.setBoxCornerRadii(5, 5, 5, 5);
		e.setErrorEnabled(true);
		e.setHintAnimationEnabled(true);
		
		e.addView(user);
		((LinearLayout)l1).addView(e);
		((LinearLayout)l2).removeView(token); 
		final com.google.android.material.textfield.TextInputLayout e1 = new com.google.android.material.textfield.TextInputLayout (this);
		e1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)); 
		
		e1.setPadding((int)getDip(2), (int)getDip(2) , (int)getDip(2) , (int)getDip(2) );
		e1.setGravity(Gravity.CENTER);
		e1.setHintEnabled(true);
		e1.setHint("Password");
		e1.setBoxBackgroundMode(e1.BOX_BACKGROUND_OUTLINE); 
		e1.setBoxStrokeColor(0xFF070707);
		e1.setBoxCornerRadii(5, 5, 5, 5);
		e1.setErrorEnabled(true);
		e1.setHintAnimationEnabled(true);
		
		e1.addView(token);
		((LinearLayout)l2).addView(e1);
		((LinearLayout)l3).removeView(valid); 
		final com.google.android.material.textfield.TextInputLayout e2 = new com.google.android.material.textfield.TextInputLayout (this);
		e2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)); 
		
		e2.setPadding((int)getDip(2), (int)getDip(2) , (int)getDip(2) , (int)getDip(2) );
		e2.setGravity(Gravity.CENTER);
		e2.setHintEnabled(true);
		e2.setHint("Validity");
		e2.setBoxBackgroundMode(e2.BOX_BACKGROUND_OUTLINE); 
		e2.setBoxStrokeColor(0xFF070707);
		e2.setBoxCornerRadii(5, 5, 5, 5);
		e2.setErrorEnabled(true);
		e2.setHintAnimationEnabled(true);
		
		e2.addView(valid);
		((LinearLayout)l3).addView(e2);
		
		{
			android.graphics.drawable.GradientDrawable SketchUi = new android.graphics.drawable.GradientDrawable();
			int d = (int) getApplicationContext().getResources().getDisplayMetrics().density;
			int clrs [] = {0xFF070707,0xFF070707};
			SketchUi= new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT, clrs);
			SketchUi.setCornerRadius(d*9);
			SketchUi.setStroke(d*0,0xFF070707);
			button1.setElevation(d*10);
			android.graphics.drawable.RippleDrawable SketchUiRD = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{0xFF303030}), SketchUi, null);
			button1.setBackground(SketchUiRD);
			button1.setClickable(true);
		}
		
	}
	private ProgressDialog prog;
	{
	}
	
	public void _dialogTheme() {
	}
	// setTheme() should be set before setContentView() so a small hack to do this in sketchware
	@Override 
	public void setContentView( int layoutResID) {
		if(getIntent().getBooleanExtra("dialogTheme",true)){
			supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
			setTheme(R.style.Theme_AppCompat_Light_Dialog);
			setFinishOnTouchOutside(true);
			
			//change true to false if you want to make dialog non cancellable when clicked outside
			//if you want to use this without app compat  change supportRequestWindowFeature() and setTheme() to below codes.
			/*
requestWindowFeature(Window.FEATURE_NO_TITLE);
setTheme(android.R.style.Theme_Dialog);
*/
			// Calling this allows the Activity behind this one to be seen again. Once all such Activities have been redrawn
			try {
				java.lang.reflect.Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions"); getActivityOptions.setAccessible(true);
				Object options = getActivityOptions.invoke(this); Class<?>[] classes = Activity.class.getDeclaredClasses(); Class<?> translucentConversionListenerClazz = null; 
				for (Class clazz : classes) { if (clazz.getSimpleName().contains("TranslucentConversionListener")) { translucentConversionListenerClazz = clazz; } } 
				java.lang.reflect.Method convertToTranslucent = Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz, ActivityOptions.class); convertToTranslucent.setAccessible(true); convertToTranslucent.invoke(this, null, options); } catch (Throwable t) {
			}
		}
		super.setContentView(layoutResID);  
	}
	{
	}
	
	
	public void _ProgresbarShow(final String _title) {
		prog = new ProgressDialog(AddUserActivity.this);
		prog.setMax(100);
		prog.setMessage(_title);
		prog.setIndeterminate(true);
		prog.setCancelable(false);
		prog.show();
	}
	
	
	public void _ProgresbarDimiss() {
		if(prog != null)
		{
			prog.dismiss();
		}
	}
	
	
	public void _AddClickEffectAt(final View _view) {
		ScaleAnimation fade_in = new ScaleAnimation(0.9f, 1f, 0.9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f); fade_in.setDuration(300); fade_in.setFillAfter(true); _view.startAnimation(fade_in);
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}