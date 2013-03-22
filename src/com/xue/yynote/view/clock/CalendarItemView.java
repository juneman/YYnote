package com.xue.yynote.view.clock;

import java.util.Date;
import java.util.Locale;

import com.xue.yynote.R;
import com.xue.yynote.tools.Lunar;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarItemView extends LinearLayout{
	private TextView mGregorian;
	private TextView mLunar;
	private int mMonth;
	private int mYear;
	private int mDay;
	
	public CalendarItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		inflate(context, R.layout.activity_calendar_item, this);
		this.initResource();
	}
	
	public CalendarItemView(Context context, Date date, int curYear, int curMonth){
		this(context);
		this.fillText(date, curYear, curMonth);
	}

	private void initResource(){
		this.mGregorian = (TextView)findViewById(R.id.calendar_gregorian);
		this.mLunar = (TextView)findViewById(R.id.calendar_lunar);
	}
	
	@SuppressWarnings("deprecation")
	private void fillText(Date date, int curYear, int curMonth) {
		// TODO Auto-generated method stub
		this.mMonth = date.getMonth();
		this.mYear = date.getYear() + 1900;
		this.mDay = date.getDate();

		this.mGregorian.setText(String.valueOf(date.getDate()));
		String lan = Locale.getDefault().getLanguage();
		if(lan.equals("zh")){
			Lunar lunar = new Lunar(date);
			this.mLunar.setText(lunar.toString());
		}
	}
	public int getDay(){
		return this.mDay;
	}
	public int getMonth(){
		return this.mMonth;
	}
	
	public int getYear(){
		return this.mYear;
	}
	
	public boolean isEquls(CalendarItemView item){
		if(this.mYear == item.getYear() && this.mMonth == item.getMonth() 
				&& this.mDay == item.getDay())
			return true;
		else return false;
	}
	
}
