package com.modxlab.admin;

import android.animation.*;
import android.app.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.os.Bundle;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.io.InputStream;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;

public class SellerListFragmentActivity extends Fragment {
	
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	
	private HashMap<String, Object> map = new HashMap<>();
	
	private ArrayList<String> list = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> listmap1 = new ArrayList<>();
	
	private LinearLayout linear1;
	private ListView listview1;
	
	private Intent intent = new Intent();
	private AlertDialog.Builder dialog;
	private DatabaseReference Seller = _firebase.getReference("Seller");
	private ChildEventListener _Seller_child_listener;
	private SharedPreferences key;
	
	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater _inflater, @Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {
		View _view = _inflater.inflate(R.layout.seller_list_fragment, _container, false);
		initialize(_savedInstanceState, _view);
		FirebaseApp.initializeApp(getContext());
		AdminApi.ensureSignedIn(null); // SECURITY: guarantee an admin session before any write
		initializeLogic();
		return _view;
	}
	
	private void initialize(Bundle _savedInstanceState, View _view) {
		linear1 = _view.findViewById(R.id.linear1);
		listview1 = _view.findViewById(R.id.listview1);
		dialog = new AlertDialog.Builder(getActivity());
		key = getContext().getSharedPreferences("key", Activity.MODE_PRIVATE);
		
		_Seller_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				Seller.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						listmap1 = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								listmap1.add(_map);
							}
						} catch (Exception _e) {
							_e.printStackTrace();
						}
						list.add(_childKey);
						listview1.setAdapter(new Listview1Adapter(listmap1));
						((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				Seller.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						listmap1 = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								listmap1.add(_map);
							}
						} catch (Exception _e) {
							_e.printStackTrace();
						}
						list.add(_childKey);
						listview1.setAdapter(new Listview1Adapter(listmap1));
						((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
			}
			
			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {
				
			}
			
			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				Seller.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						listmap1 = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								listmap1.add(_map);
							}
						} catch (Exception _e) {
							_e.printStackTrace();
						}
						list.add(_childKey);
						listview1.setAdapter(new Listview1Adapter(listmap1));
						((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
			}
			
			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		Seller.addChildEventListener(_Seller_child_listener);
	}
	
	private void initializeLogic() {
	}
	
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getActivity().getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.user, null);
			}
			
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final LinearLayout linear2 = _view.findViewById(R.id.linear2);
			final ImageView imageview2 = _view.findViewById(R.id.imageview2);
			final ImageView edit = _view.findViewById(R.id.edit);
			final ImageView delete = _view.findViewById(R.id.delete);
			final LinearLayout linear4 = _view.findViewById(R.id.linear4);
			final TextView username = _view.findViewById(R.id.username);
			final LinearLayout linear3 = _view.findViewById(R.id.linear3);
			final TextView num = _view.findViewById(R.id.num);
			final ImageView imageview1 = _view.findViewById(R.id.imageview1);
			final LinearLayout status = _view.findViewById(R.id.status);
			final TextView textview1 = _view.findViewById(R.id.textview1);
			
			if (_data.get((int)_position).containsKey("user")) {
				username.setText(_data.get((int)_position).get("user").toString());
			}
			if (_data.get((int)_position).containsKey("status")) {
				if (_data.get((int)_position).get("status").toString().equals("true")) {
					status.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)100, 0xFF00FF00));
				} else {
					status.setBackground(new GradientDrawable() { public GradientDrawable getIns(int a, int b) { this.setCornerRadius(a); this.setColor(b); return this; } }.getIns((int)100, 0xFFFF0000));
				}
			}
			imageview1.setImageResource(R.drawable.revendedor);
			num.setText(String.valueOf((long)(_position + 1)));
			delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					dialog.setTitle("Delete User");
					dialog.setMessage("Are You Sure To Delete User");
					dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							// SECURITY: delete now goes through the "deleteSeller" Cloud Function.
							final int _delPos = (int)(_position);
							AdminApi.deleteSeller(list.get(_delPos), new AdminApi.Callback() {
								@Override
								public void onSuccess(java.util.HashMap<String, Object> data) {
									list.remove(_delPos);
								}
								@Override
								public void onError(String message) {
									SketchwareUtil.showMessage(getApplicationContext(), "Failed: " + message);
								}
							});
						}
					});
					dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							
						}
					});
					dialog.create().show();
				}
			});
			linear1.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View _view) {
					if (_data.get((int)_position).get("status").toString().equals("true")) {
						dialog.setMessage("Are You Sure To Deactivate User ?");
						dialog.setPositiveButton("Deactivate", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface _dialog, int _which) {
								if (_data.get((int)_position).containsKey("key")) {
									// SECURITY: status change now goes through the "toggleSellerStatus" Cloud Function.
									AdminApi.toggleSellerStatus(_data.get((int)_position).get("key").toString(), false, new AdminApi.Callback() {
										@Override
										public void onSuccess(java.util.HashMap<String, Object> data) {}
										@Override
										public void onError(String message) { SketchwareUtil.showMessage(getApplicationContext(), "Failed: " + message); }
									});
								}
							}
						});
					} else {
						dialog.setMessage("Are You Sure To Activate User ?");
						dialog.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface _dialog, int _which) {
								if (_data.get((int)_position).containsKey("key")) {
									// SECURITY: status change now goes through the "toggleSellerStatus" Cloud Function.
									AdminApi.toggleSellerStatus(_data.get((int)_position).get("key").toString(), true, new AdminApi.Callback() {
										@Override
										public void onSuccess(java.util.HashMap<String, Object> data) {}
										@Override
										public void onError(String message) { SketchwareUtil.showMessage(getApplicationContext(), "Failed: " + message); }
									});
								}
							}
						});
					}
					dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							
						}
					});
					dialog.create().show();
					return true;
				}
			});
			edit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					intent.setClass(getContext().getApplicationContext(), EditSellerActivity.class);
					key.edit().putString("key", _data.get((int)_position).get("key").toString()).commit();
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
			
			return _view;
		}
	}
}