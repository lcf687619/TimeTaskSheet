package com.mission.schedule.constants;

public class PostSendMainActivity {
	private int mMsg;
	private int index;
	public PostSendMainActivity(int index, int msg) {
		this.mMsg = msg;
		this.index = index;
	}
	public int getMsg(){
		return mMsg;
	}
	public int getIndex() {
		return index;
	}
}
