package com.musselwhizzle.dispatcher.models;

import android.os.Handler;

import com.musselwhizzle.dispatcher.events.EventDispatcher;
import com.musselwhizzle.dispatcher.events.TimerEvent;

public class TickModel extends EventDispatcher {
	
	private long started = 0;
	private int count = 0;
	private Handler handler;
	
	public TickModel() {
		super();
		started = System.currentTimeMillis();
		
		handler = new Handler();
		handler.postDelayed(task, 1000);
	}
	
	private Runnable task = new Runnable() {
		
		@Override
		public void run() {
			long passed = System.currentTimeMillis() - started;
			TimerEvent e = new TimerEvent(TimerEvent.TIMER, passed, count);
			dispatchEvent(e);
			count++;
			handler.postDelayed(task, 1000);
		}
	};
}
