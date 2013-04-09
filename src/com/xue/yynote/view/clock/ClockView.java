package com.xue.yynote.view.clock;

import java.util.Date;

import com.xue.yynote.R;
import com.xue.yynote.model.ClockModel;
import com.xue.yynote.view.clock.ClockCalendarView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class ClockView extends LinearLayout{
	public static final String TAG = "ClockView";
	private LinearLayout mDays;
	private TextView tvDays;
	
	private LinearLayout mMinutes;
	private TextView tvMinutes;
	
	private LinearLayout mAlertInterval;
	private TextView tvAlertInterval;
	
	private LinearLayout mAlertTimes;
	private TextView tvAlertTimes;
	
	private ClockModel mClockModel;
	private Date time;
	private String[] arrayAlertTimes = this.getContext().getResources().getStringArray(R.array.clock_alert_times_array);
	private String[] arrayAlertInterval = this.getContext().getResources().getStringArray(R.array.clock_alert_interval_array);
	
	private Drawable mAlertItemBgDrawable;
	
	private OnTouchListener mTouchListener = new OnTouchListener(){

		@SuppressWarnings("deprecation")
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				if(ClockView.this.mAlertItemBgDrawable == null){
					ClockView.this.mAlertItemBgDrawable = v.getBackground();
				}
				v.setBackgroundColor(Color.GREEN);
				break;
			case MotionEvent.ACTION_UP: 
				v.setBackgroundDrawable(ClockView.this.mAlertItemBgDrawable);
				break;
			}
			return false;
		}
		
	};
	public ClockView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		inflate(context, R.layout.activity_clock, this);
		initResource();
		
	}
	private void initResource() {
		// TODO Auto-generated method stub
		this.time = new Date();
		this.mAlertItemBgDrawable = null;
		this.mDays = (LinearLayout)findViewById(R.id.clock_days);
		this.tvDays = (TextView)findViewById(R.id.clock_days_tv);
		
		this.mMinutes = (LinearLayout)findViewById(R.id.clock_minutes);
		this.tvMinutes = (TextView)findViewById(R.id.clock_minutes_tv);
		
		this.mAlertInterval = (LinearLayout)findViewById(R.id.clock_alert_terminal);
		this.tvAlertInterval = (TextView)findViewById(R.id.clock_alert_terminal_tv);
		
		this.mAlertTimes = (LinearLayout)findViewById(R.id.clock_alert_times);
		this.tvAlertTimes = (TextView)findViewById(R.id.clock_alert_times_tv);
		
		this.mDays.setOnTouchListener(mTouchListener);
		this.mMinutes.setOnTouchListener(mTouchListener);
		this.mAlertInterval.setOnTouchListener(mTouchListener);
		this.mAlertTimes.setOnTouchListener(mTouchListener);
		
		this.mDays.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClockView.this.selectDays();				
			}
			
		});
		
		this.mMinutes.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClockView.this.selectMinutes();
			}
			
		});
		
		this.mAlertInterval.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClockView.this.selectAlertInterval();
			}
			
		});
		
		this.mAlertTimes.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClockView.this.selectAlertTimes();
			}
			
		});
	}
	protected void selectAlertTimes() {
		// TODO Auto-generated method stub
		
		new AlertDialog.Builder(this.getContext()).
			setTitle(R.string.clock_alert_days).
			setItems(arrayAlertTimes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int[] a = new int[]{
						1, 2, 3, 5, 10	
					};
					ClockView.this.tvAlertTimes.setText(arrayAlertTimes[which]);
					ClockView.this.mClockModel.setAlertTimes(a[which]);
					if(which > 0 && ClockView.this.mClockModel.getAlertInterval() == 0){
						ClockView.this.mClockModel.setAlertInterval(1);
						ClockView.this.tvAlertInterval.setText(arrayAlertInterval[1]);
					}
				}
			}).show();
	}

	protected void selectAlertInterval() {
		// TODO Auto-generated method stub
		
		new AlertDialog.Builder(this.getContext()).
			setTitle(R.string.clock_alert_days).
			setItems(arrayAlertInterval, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int[] a = new int[]{
						0, 1, 2, 3, 5, 10	
					};
					ClockView.this.tvAlertInterval.setText(arrayAlertInterval[which]);
					ClockView.this.mClockModel.setAlertInterval(a[which]);
				}
			}).show();
	}

	@SuppressWarnings("deprecation")
	protected void selectMinutes() {
		// TODO Auto-generated method stub
		LinearLayout mTimeView = 
				(LinearLayout) LayoutInflater.from(ClockView.this.getContext()).
				inflate(R.layout.activity_clock_time, null);
		
		final TimePicker localTimePicker = (TimePicker)mTimeView.findViewById(R.id.clock_timepicker);
        localTimePicker.setIs24HourView(Boolean.valueOf(true));
        localTimePicker.setCurrentHour(Integer.valueOf(this.time.getHours()));
        localTimePicker.setCurrentMinute(Integer.valueOf(this.time.getMinutes()));
		new AlertDialog.Builder(ClockView.this.getContext()).
			setTitle(R.string.clock_minutes).
			setView(mTimeView).
			setPositiveButton(R.string.clock_dialog_ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ClockView.this.time.setHours(localTimePicker.getCurrentHour());
					ClockView.this.time.setMinutes(localTimePicker.getCurrentMinute());
					ClockView.this.time.setSeconds(0);
		            ClockView.this.tvMinutes.setText(DateUtils.formatDateTime(ClockView.this.getContext(),
		            		ClockView.this.time.getTime(),
		                    DateUtils.FORMAT_24HOUR | DateUtils.FORMAT_SHOW_TIME));
		            ClockView.this.mClockModel.setTimeInMillis(ClockView.this.time.getTime());
				}
				
			}).
			setNegativeButton(R.string.clock_dialog_cancel, null).show();
	}

	@SuppressLint("HandlerLeak")
	@SuppressWarnings("deprecation")
	protected void selectDays() {
		// TODO Auto-generated method stub
		
		final ClockCalendarView mClockCalendar = new ClockCalendarView(this.getContext());

		final Handler initCalendarViewHandler = new Handler(){
			public void handleMessage(Message message){
				
				mClockCalendar.initGridView();
			}
		};
		
		new AlertDialog.Builder(ClockView.this.getContext()).
			setTitle(R.string.clock_days).
			setView(mClockCalendar).
			setPositiveButton(R.string.clock_dialog_ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					long millis = mClockCalendar.getDate();
					Date date = new Date(millis);
					ClockView.this.time.setYear(date.getYear());
					ClockView.this.time.setMonth(date.getMonth());
					ClockView.this.time.setDate(date.getDate());
					ClockView.this.tvDays.setText(DateUtils.formatDateTime(ClockView.this.getContext(),
							millis, DateUtils.FORMAT_SHOW_DATE
			                | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
					ClockView.this.mClockModel.setTimeInMillis(ClockView.this.time.getTime());
				}
				
			}).
			setNegativeButton(R.string.clock_dialog_cancel, null).show();
		Log.d(TAG, "finish show");
		initCalendarViewHandler.sendEmptyMessageDelayed(0, 500);
	}

	public void setClockModel(ClockModel clockModel){
		this.mClockModel = clockModel;
		if(this.mClockModel.getTimeInMillis() > 0)
			this.time = new Date(this.mClockModel.getTimeInMillis());
		this.tvDays.setText(DateUtils.formatDateTime(this.getContext(),
                this.time.getTime(), DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR));
		
		this.tvMinutes.setText(DateUtils.formatDateTime(ClockView.this.getContext(),
                this.time.getTime(),
                DateUtils.FORMAT_24HOUR | DateUtils.FORMAT_SHOW_TIME));
		Resources mResources = this.getContext().getResources();
		String alertTimes = String.valueOf(mClockModel.getAlertTimes()) + " ";
		alertTimes += 
				mResources.getString(mClockModel.getAlertTimes() > 1 ? R.string.clock_alert_multi_times : R.string.clock_alert_single_time);
		this.tvAlertTimes.setText(alertTimes);
		
		if(mClockModel.getAlertInterval() > 0){
			String alertInterval = String.valueOf(mClockModel.getAlertInterval()) + " ";
			
			alertInterval +=  
					mResources.getString(mClockModel.getAlertInterval() > 1 ? R.string.clock_alert_multi_mins : R.string.clock_alert_single_min);
			this.tvAlertInterval.setText(alertInterval);
		}
		else this.tvAlertInterval.setText(R.string.clock_none);
	}
	
	public long getClockTime(){
		return this.time.getTime();
	}
}
