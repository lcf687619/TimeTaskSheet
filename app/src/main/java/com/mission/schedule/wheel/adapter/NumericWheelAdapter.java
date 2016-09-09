/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mission.schedule.wheel.adapter;

import com.mission.schedule.wheel.WheelView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter extends AbstractWheelTextAdapter {

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	// Values
	private int minValue;
	private int maxValue;

	// format
	private String format;
	
	private int dataType=0;//0其他,1分钟
	private int currentItem;
	
	private WheelView wheelView;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 */
//	public NumericWheelAdapter(Context context) {
//		this(context, DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
//	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 */
	public NumericWheelAdapter(Context context, WheelView wheelView, int minValue, int maxValue) {
		this(context, wheelView, minValue, maxValue, null,0);
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the current context
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 * @param format
	 *            the format string
	 */
	public NumericWheelAdapter(Context context, WheelView wheelView, int minValue, int maxValue, String format, int dataType) {
		super(context);
		this.wheelView = wheelView;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
		this.dataType = dataType;
	}

	@Override
	public CharSequence getItemText(int index) {
		if (index >= 0 && index < getItemsCount()) {
			int value =0;
			if(this.dataType==1){
				value = minValue + index*5;
			}else
				value = minValue + index;
			return format != null ? String.format(format, value) : Integer.toString(value);
		}
		return null;
	}
	
	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		currentItem = index;
		return super.getItem(index, convertView, parent);
	}

	@Override
	public int getItemsCount() {
		return maxValue - minValue + 1;
	}
}
