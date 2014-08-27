package com.gharseldin.taskorganizer.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gharseldin.taskorganizer.R;
import com.gharseldin.taskorganizer.R.id;
import com.gharseldin.taskorganizer.R.layout;
import com.gharseldin.taskorganizer.datastructures.TaskRecord;

public class ArchiveAdapter extends BaseAdapter {

	private Context ctx;
	private Activity activity;
	private TaskRecord[] tasks;

	private TextView setDate, taskContent, completeStatus;

	public ArchiveAdapter(Activity activity, TaskRecord[] tasks) {
		this.activity = activity;
		this.tasks = tasks;
	}

	@Override
	public int getCount() {

		return tasks.length;

	}

	@Override
	public Object getItem(int position) {

		return position;

	}

	@Override
	public long getItemId(int position) {

		return position;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = activity.getLayoutInflater();
		convertView = inflater.inflate(R.layout.archive_row, null);

		setDate = (TextView) convertView.findViewById(R.id.archived_task_date);
		taskContent = (TextView) convertView.findViewById(R.id.archived_task);
		completeStatus = (TextView) convertView.findViewById(R.id.archived_task_state);
		
		setDate.setText(tasks[position].getDateSet());
		taskContent.setText(tasks[position].getContent());
		completeStatus.setText(tasks[position].getStatus());
		
		return convertView;
	}
}
