package com.xue.yynote.view.clock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xue.yynote.R;
import com.xue.yynote.view.clock.CalendarItemView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class ClockCalendarView  extends LinearLayout{
	public static final String TAG = "ClockCalendarView";
	public static final int daysOfMonth = 35;
	public static final long dayOfMillisecond = 24 * 60 * 60 * 1000;
	public static final int CURRENT_MONTH_BG = 0xFF7c8577;
	public static final int CURRENT_DAY_BG = 0xFFFFFFFF;
	private final String[] monthName = this.getContext().getResources().getStringArray(R.array.clock_calendar_month);
	
	private CalendarAdapter mAdapter;
	private TextView mTvCurYear;
	private TextView mTvCurMonth;
	private int mCurYear;
	private int mCurMonth;
	private GridView mGridView;
	private long mDate;
	private Drawable mCalendarItemBgDrawable;
	
	private CalendarItemView curItemView;
	private ArrayList<Long> days;
	private int curDayPosition;
	
	@SuppressLint("HandlerLeak")
	private Handler updateMonthBgHandler = new Handler(){
		public void handleMessage(Message message){
			ClockCalendarView.this.updateCurMonthBg(message.arg1, message.arg2);
		}
	};
	public ClockCalendarView(Context context) {
		super(context);
		
		inflate(context, R.layout.activity_clock_calendar, this);
		this.initResource();
	}
	
	@SuppressWarnings("deprecation")
	private void initAdapter() {
		// TODO Auto-generated method stub
		this.days = new ArrayList<Long>();
		Date date = new Date();
		int day = date.getDate() -1;
		Log.d(TAG, ""+day);
		int week = date.getDay();
		Log.d(TAG, ""+week);
		week = (week + 7 - (day+1) % 7 + 1) % 7;
		Log.d(TAG, ""+week);
		for(int i = week - 1; i >= 0; i --){
			days.add((long) 0);
		}
		days.add((date.getTime() - day * ClockCalendarView.dayOfMillisecond));
		for(int i = week - 1; i >= 0; i --){
			days.set(i, days.get(i+1) - ClockCalendarView.dayOfMillisecond);
		}
		
		for(int i= week + 1; i < (42 + daysOfMonth); i++){
			days.add(days.get(i-1) + ClockCalendarView.dayOfMillisecond);
		}
		this.curDayPosition = week + day;
	}
	
	@SuppressWarnings("deprecation")
	private void initResource() {
		// TODO Auto-generated method stub
		this.mDate = System.currentTimeMillis();
		this.mCalendarItemBgDrawable = null;
		this.initAdapter();
		this.mAdapter = new CalendarAdapter(this.getContext(), R.layout.activity_calendar_item, this.days);
		
		this.mGridView = (GridView) findViewById(R.id.clock_calendar_gridview);
		this.mGridView.setAdapter(this.mAdapter);
		
		this.mTvCurYear = (TextView)findViewById(R.id.clock_calendar_year);
		this.mTvCurMonth = (TextView)findViewById(R.id.clock_calendar_month);
		this.mAdapter.setCurPosition(this.curDayPosition);
		this.mGridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Log.d(TAG, "GridView onItemClick " + position);
				long millis = (Long) ClockCalendarView.this.mAdapter.getItem(position);
				ClockCalendarView.this.mDate = millis;
				CalendarItemView curItem = (CalendarItemView) ClockCalendarView.this.mGridView.getChildAt(
						ClockCalendarView.this.curDayPosition - 
						ClockCalendarView.this.mGridView.getFirstVisiblePosition());
						
				if(curItem != null){
					if(curItem.getYear() == ClockCalendarView.this.mCurYear &&
							curItem.getMonth() == ClockCalendarView.this.mCurMonth){
						curItem.setBackgroundColor(CURRENT_MONTH_BG);
					} else {
						curItem.setBackgroundDrawable(ClockCalendarView.this.mCalendarItemBgDrawable);
					}
				}
				ClockCalendarView.this.curItemView = curItem;
				ClockCalendarView.this.curDayPosition = position;
				view.setBackgroundDrawable(ClockCalendarView.this.mCalendarItemBgDrawable);
			}
			
		});
		this.mGridView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				// 当不滚动时
			    if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			    	CalendarItemView item = (CalendarItemView) view.getChildAt(28);
			    	
			    	Log.d(TAG, "year:" + item.getYear() + " month:" + item.getMonth() + " day:" + item.getDay());
			    	ClockCalendarView.this.updateYearAndMonth(item.getYear(), item.getMonth());
			    	ClockCalendarView.this.updateCurMonthBg(item.getYear(), item.getMonth());
			    	
			    	//判断是否滚动到底部
			    	if (view.getLastVisiblePosition() == view.getCount() - 1) {
				    	Log.d(TAG, "scroll to bottom");
			    		ClockCalendarView.this.expandData();
			    	}
			    }
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				//Log.d(TAG, "gridview onscroll");
			}
			
		});
	}
	@SuppressWarnings("deprecation")
	public void initGridView() {
		// TODO Auto-generated method stub
		Log.d(TAG, "initGridView");
		this.curItemView = (CalendarItemView) this.mGridView.getChildAt(this.curDayPosition);
		this.mCalendarItemBgDrawable = this.curItemView.getBackground();
		Date date = new Date(this.mDate);

		this.updateYearAndMonth(date.getYear() + 1900, date.getMonth());
		this.updateCurMonthBg(this.curItemView.getYear(), this.curItemView.getMonth());
	}
	@SuppressWarnings("deprecation")
	private void updateCurMonthBg(int year, int month) {
		// TODO Auto-generated method stub
		int p = 0;
		Log.d(TAG, "updateCurMonthBg");
		int firstVisiblePosition = this.mGridView.getFirstVisiblePosition();
		CalendarItemView item = (CalendarItemView) this.mGridView.getChildAt(p);
		while(item!= null && (item.getYear() != year || item.getMonth() != month)) {
			if((p + firstVisiblePosition) == this.curDayPosition){
				item.asCurrentDay();
			}
			else {
				item.setBackgroundDrawable(this.mCalendarItemBgDrawable);
			}
			item = (CalendarItemView) this.mGridView.getChildAt(++p);
			//Log.d(TAG, "" + p + "year:" + item.getYear() + " month:" + item.getMonth() + " day:" + item.getDay());
		}
		
		item = (CalendarItemView) this.mGridView.getChildAt(p);
		while(item != null && item.getYear() == year && item.getMonth() == month){
			if((p + firstVisiblePosition) == this.curDayPosition){
				item.asCurrentDay();
			}
			else {
				item.setBackgroundColor(CURRENT_MONTH_BG);
			}
			//Log.d(TAG, "" + p + "year:" + item.getYear() + " month:" + item.getMonth() + " day:" + item.getDay());
			item = (CalendarItemView) this.mGridView.getChildAt(++p);
		}
		
		while(item != null && p < this.mGridView.getCount()){
			if((p + firstVisiblePosition) == this.curDayPosition){
				item.asCurrentDay();
			}
			else {
				item.setBackgroundDrawable(this.mCalendarItemBgDrawable);
			}
			item = (CalendarItemView) this.mGridView.getChildAt(++p);
		}
	}
	public long getDate() {
		// TODO Auto-generated method stub
		return this.mDate;
	}
	
	public void updateYearAndMonth(int year, int month){
		this.mCurYear = year;
		this.mCurMonth = month;
		this.mTvCurYear.setText(String.valueOf(year));
		this.mTvCurMonth.setText(monthName[month]);
		this.mAdapter.setCurYearAndMonth(year, month);
	}
	public void expandData(){
		long lastItem = this.days.get(this.days.size() - 1 ) + ClockCalendarView.dayOfMillisecond;
		for(int i = 0;i < daysOfMonth;i++){
			this.days.add(lastItem + ClockCalendarView.dayOfMillisecond * i);
			//this.mAdapter.add(lastItem + ClockCalendarView.dayOfMillisecond * i);
		}
		this.mAdapter.notifyDataSetChanged();
		CalendarItemView item = (CalendarItemView) this.mGridView.getChildAt(28);
		Message msg = new Message();
		msg.arg1 = item.getYear();
		msg.arg2 = item.getMonth();
		this.updateMonthBgHandler.sendMessageDelayed(msg, 300);
	}
}

class CalendarAdapter extends ArrayAdapter<Long> {
	public static final String TAG = "CalendarAdapter";
	
	private int mMonth;
	private int mYear;
	public CalendarAdapter(Context context, int resource, List<Long> objects){
		super(context, resource, objects);
	}
	
	public void setCurPosition(int position){
	}
	
	public void setCurYearAndMonth(int year, int month){
		this.mYear = year;
		this.mMonth = month;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CalendarItemView mItemView;
		Date date = new Date((Long) this.getItem(position));
		mItemView  = new CalendarItemView(this.getContext(), date, mYear, mMonth);

        return mItemView;
	}
}
