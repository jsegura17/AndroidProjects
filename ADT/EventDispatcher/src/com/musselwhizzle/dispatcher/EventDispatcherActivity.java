package com.musselwhizzle.dispatcher;

import java.util.Random;

import com.musselwhizzle.dispatcher.events.Event;
import com.musselwhizzle.dispatcher.events.EventListener;
import com.musselwhizzle.dispatcher.events.TimerEvent;
import com.musselwhizzle.dispatcher.models.FooModel;
import com.musselwhizzle.dispatcher.models.TickModel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventDispatcherActivity extends Activity {
    
	private FooModel fooModel;
	private TickModel tickModel;
	private TextView labelField;
	private TextView countField;
	private TextView tickField;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // reference views
        Button labelBtn = (Button)findViewById(R.id.labelBtn);
        Button countBtn = (Button)findViewById(R.id.countBtn);
        Button removeBtn = (Button)findViewById(R.id.removeBtn);
        labelField = (TextView)findViewById(R.id.labelField);
        countField = (TextView)findViewById(R.id.countField);
        tickField = (TextView)findViewById(R.id.tickField);
        
        // give some intial data
        fooModel = new FooModel();
        fooModel.setMyLabel("First Label");
        fooModel.setMyCount(5);
        tickModel = new TickModel();
        
        // bind our views to the initial data
        labelField.setText("Label: " + fooModel.getMyLabel());
        countField.setText("Count: " + fooModel.getMyCount());
        
        // here we add our observers/listeners for data changes on the model
        fooModel.addListener(FooModel.ChangeEvent.MY_LABEL_CHANGED, labelChangedListener);
        fooModel.addListener(FooModel.ChangeEvent.MY_COUNT_CHANGED, countChangedListener);
        tickModel.addListener(TimerEvent.TIMER, new EventListener() {
			@Override
			public void onEvent(Event event) {
				TimerEvent e = (TimerEvent)event;
				tickField.setText("passed: " + e.getTimePassed() + ", count: " + e.getCount());
			}
		});
        
        
        // make our buttons do stuff
        labelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] items = new String[]{"item1", "item2", "item3", "item4"};
				int ran = new Random().nextInt(4);
				fooModel.setMyLabel(items[ran]);
			}
		});
        
        countBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		fooModel.setMyCount(fooModel.getMyCount()+1);
        	}
        });
        
        removeBtn.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		fooModel.removeListener(FooModel.ChangeEvent.MY_LABEL_CHANGED, labelChangedListener);
                fooModel.removeListener(FooModel.ChangeEvent.MY_COUNT_CHANGED, countChangedListener);
        	}
        });
    }
	
	// this is our action/event listeners. They get called when a property changes. 
	private EventListener labelChangedListener = new EventListener() {
		@Override
		public void onEvent(Event event) {
			labelField.setText("Label: " + fooModel.getMyLabel());
		}
	};
	
	private EventListener countChangedListener = new EventListener() {
		@Override
		public void onEvent(Event event) {
			countField.setText("Count: " + fooModel.getMyCount());
		}
	};
}