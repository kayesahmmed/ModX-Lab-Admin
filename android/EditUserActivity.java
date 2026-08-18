package com.modxlab.admin;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.content.*;
import android.content.SharedPreferences;
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
import com.google.android.material.button.*;
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
import java.util.*;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class EditUserActivity extends AppCompatActivity {
	
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private HashMap<String, Object> map = new HashMap<>();
	private HashMap<String, Object> map1 = new HashMap<>();
	
	private LinearLayout linear1;
	private LinearLayout linear2;
	private LinearLayout linear3;
	private MaterialButton materialbutton1;
	private ImageView imageview1;
	private TextView textview1;
	private LinearLayout linear4;
	private LinearLayout linear12;
	private LinearLayout linear5;
	private LinearLayout linear6;
	private LinearLayout linear7;
	private LinearLayout linear8;
	private LinearLayout linear9;
	private LinearLayout linear13;
	private LinearLayout linear14;
	private LinearLayout linear16;
	private LinearLayout linear22;
	private LinearLayout linear23;
	private LinearLayout linear17;
	private LinearLayout linear10;
	private LinearLayout linear11;
	private LinearLayout linear18;
	private LinearLayout linear21;
	private LinearLayout linear20;
	private LinearLayout linear15;
	private ImageView imageview2;
	private TextView t_name;
	private TextView textview2;
	private TextView t_key;
	private TextView textview3;
	private EditText edittext1;
	private TextView textview4;
	private EditText edittext2;
	private TextView textview5;
	private TextView t_device;
	private TextView textview10;
	private TextView t_version;
	private TextView textview6;
	private TextView t_rgtime;
	private TextView textview7;
	private TextView t_expire;
	private TextView textview9;
	private TextView t_status;
	private TextView textview8;
	private TextView t_b;
	
	private DatabaseReference User = _firebase.getReference("User");
	private ChildEventListener _User_child_listener;
	private SharedPreferences key;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.edit_user);
		initialize(_savedInstanceState);
		FirebaseApp.initializeApp(this);
		AdminApi.ensureSignedIn(null); // SECURITY: guarantee an admin session before any write
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		linear2 = findViewById(R.id.linear2);
		linear3 = findViewById(R.id.linear3);
		materialbutton1 = findViewById(R.id.materialbutton1);
		imageview1 = findViewById(R.id.imageview1);
		textview1 = findViewById(R.id.textview1);
		linear4 = findViewById(R.id.linear4);
		linear12 = findViewById(R.id.linear12);
		linear5 = findViewById(R.id.linear5);
		linear6 = findViewById(R.id.linear6);
		linear7 = findViewById(R.id.linear7);
		linear8 = findViewById(R.id.linear8);
		linear9 = findViewById(R.id.linear9);
		linear13 = findViewById(R.id.linear13);
		linear14 = findViewById(R.id.linear14);
		linear16 = findViewById(R.id.linear16);
		linear22 = findViewById(R.id.linear22);
		linear23 = findViewById(R.id.linear23);
		linear17 = findViewById(R.id.linear17);
		linear10 = findViewById(R.id.linear10);
		linear11 = findViewById(R.id.linear11);
		linear18 = findViewById(R.id.linear18);
		linear21 = findViewById(R.id.linear21);
		linear20 = findViewById(R.id.linear20);
		linear15 = findViewById(R.id.linear15);
		imageview2 = findViewById(R.id.imageview2);
		t_name = findViewById(R.id.t_name);
		textview2 = findViewById(R.id.textview2);
		t_key = findViewById(R.id.t_key);
		textview3 = findViewById(R.id.textview3);
		edittext1 = findViewById(R.id.edittext1);
		textview4 = findViewById(R.id.textview4);
		edittext2 = findViewById(R.id.edittext2);
		textview5 = findViewById(R.id.textview5);
		t_device = findViewById(R.id.t_device);
		textview10 = findViewById(R.id.textview10);
		t_version = findViewById(R.id.t_version);
		textview6 = findViewById(R.id.textview6);
		t_rgtime = findViewById(R.id.t_rgtime);
		textview7 = findViewById(R.id.textview7);
		t_expire = findViewById(R.id.t_expire);
		textview9 = findViewById(R.id.textview9);
		t_status = findViewById(R.id.t_status);
		textview8 = findViewById(R.id.textview8);
		t_b = findViewById(R.id.t_b);
		key = getSharedPreferences("key", Activity.MODE_PRIVATE);
		
		materialbutton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_AddClickEffectAt(materialbutton1);
				// SECURITY: credential update now goes through the "updateUserCredentials" Cloud Function.
				AdminApi.updateUserCredentials(key.getString("key", ""), edittext1.getText().toString(), edittext2.getText().toString(), new AdminApi.Callback() {
					@Override
					public void onSuccess(java.util.HashMap<String, Object> data) {
						SketchwareUtil.showMessage(getApplicationContext(), "Updated");
					}
					@Override
					public void onError(String message) {
						SketchwareUtil.showMessage(getApplicationContext(), "Failed: " + message);
					}
				});
			}
		});
		
		t_b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_AddClickEffectAt(t_b);
				// SECURITY: status toggle now goes through the "toggleUserStatus" Cloud Function.
				final boolean _activate = t_status.getText().toString().equals("Activate");
				AdminApi.toggleUserStatus(key.getString("key", ""), _activate, new AdminApi.Callback() {
					@Override
					public void onSuccess(java.util.HashMap<String, Object> data) {
						SketchwareUtil.showMessage(getApplicationContext(), "Status Updated");
					}
					@Override
					public void onError(String message) {
						SketchwareUtil.showMessage(getApplicationContext(), "Failed: " + message);
					}
				});
			}
		});
		
		_User_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				if (_childKey.equals(key.getString("key", ""))) {
					if (_childValue.containsKey("key")) {
						t_key.setText(_childValue.get("key").toString());
					}
					if (_childValue.containsKey("user")) {
						t_name.setText(_childValue.get("user").toString());
						edittext1.setText(_childValue.get("user").toString());
					}
					if (_childValue.containsKey("pass")) {
						edittext2.setText(_childValue.get("pass").toString());
					}
					if (_childValue.containsKey("status")) {
						if (_childValue.get("status").toString().equals("true")) {
							t_status.setText("Activate");
							t_status.setTextColor(0xFF00F301);
							t_b.setText("Deactivate");
						} else {
							t_status.setText("Deactivate");
							t_status.setTextColor(0xFFFF0000);
							t_b.setText("Activate");
						}
					}
					if (_childValue.containsKey("rgtime")) {
						t_rgtime.setText(_childValue.get("rgtime").toString());
					}
					if (_childValue.containsKey("Validity")) {
						t_expire.setText(_childValue.get("Validity").toString());
					}
					if (_childValue.containsKey("access")) {
						if (_childValue.get("access").toString().equals("∞")) {
							t_device.setText("Unlimited Device Can't Show");
							t_version.setText("Unlimited Device Can't Show");
						} else {
							if (_childValue.get("device").toString().equals("null")) {
								t_device.setText("Not Authorised Yet");
							} else {
								t_device.setText(_childValue.get("device").toString());
							}
							if (_childValue.get("version").toString().equals("null")) {
								t_version.setText("Not Authorised Yet");
							} else {
								t_version.setText(_childValue.get("version").toString());
							}
						}
					}
				}
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				if (_childKey.equals(key.getString("key", ""))) {
					if (_childValue.containsKey("key")) {
						t_key.setText(_childValue.get("key").toString());
					}
					if (_childValue.containsKey("user")) {
						t_name.setText(_childValue.get("user").toString());
						edittext1.setText(_childValue.get("user").toString());
					}
					if (_childValue.containsKey("pass")) {
						edittext2.setText(_childValue.get("pass").toString());
					}
					if (_childValue.containsKey("status")) {
						if (_childValue.get("status").toString().equals("true")) {
							t_status.setText("Activate");
							t_status.setTextColor(0xFF00F301);
							t_b.setText("Deactivate");
						} else {
							t_status.setText("Deactivate");
							t_status.setTextColor(0xFFFF0000);
							t_b.setText("Activate");
						}
					}
					if (_childValue.containsKey("rgtime")) {
						t_rgtime.setText(_childValue.get("rgtime").toString());
					}
					if (_childValue.containsKey("Validity")) {
						t_expire.setText(_childValue.get("Validity").toString());
					}
					if (_childValue.containsKey("access")) {
						if (_childValue.get("access").toString().equals("∞")) {
							t_device.setText("Unlimited Device Can't Show");
							t_version.setText("Unlimited Device Can't Show");
						} else {
							if (_childValue.get("device").toString().equals("null")) {
								t_device.setText("Not Authorised Yet");
							} else {
								t_device.setText(_childValue.get("device").toString());
							}
							if (_childValue.get("version").toString().equals("null")) {
								t_version.setText("Not Authorised Yet");
							} else {
								t_version.setText(_childValue.get("version").toString());
							}
						}
					}
				}
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
		
		{
			android.graphics.drawable.GradientDrawable SketchUi = new android.graphics.drawable.GradientDrawable();
			int d = (int) getApplicationContext().getResources().getDisplayMetrics().density;
			int clrs [] = {0xFFFFFFFF,0xFFFFFFFF};
			SketchUi= new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT, clrs);
			SketchUi.setCornerRadius(d*10);
			SketchUi.setStroke(d*2,0xFFE0E0E0);
			linear3.setElevation(d*10);
			android.graphics.drawable.RippleDrawable SketchUiRD = new android.graphics.drawable.RippleDrawable(new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{0xFFFFFFFF}), SketchUi, null);
			linear3.setBackground(SketchUiRD);
			linear3.setClickable(false);
		}
		
		int[] colorsdj = { Color.parseColor("#0026FE"), Color.parseColor("#0026FE") }; android.graphics.drawable.GradientDrawable dj = new android.graphics.drawable.GradientDrawable(android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM, colorsdj);
		dj.setCornerRadii(new float[]{(int)0,(int)0,(int)15,(int)15,(int)0,(int)0,(int)15,(int)15});
		dj.setStroke((int) 2, Color.parseColor("#E0E0E0"));
		t_b.setElevation((float) 5);
		t_b.setBackground(dj);
		
		//RIPPLE ROUND BLOCK BY DJ GAMING VIP
	}
	
	public void _clickAnimation(final View _view) {
		ScaleAnimation fade_in = new ScaleAnimation(0.9f, 1f, 0.9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.7f);
		fade_in.setDuration(300);
		fade_in.setFillAfter(true);
		_view.startAnimation(fade_in);
		//aauraparti YouTube channel//
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