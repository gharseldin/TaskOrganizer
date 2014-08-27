package com.gharseldin.taskorganizer.adapters;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gharseldin.taskorganizer.R;
import com.gharseldin.taskorganizer.R.drawable;
import com.gharseldin.taskorganizer.UpdateTaskActivity;
import com.gharseldin.taskorganizer.database.TaskDatabaseManager;
import com.gharseldin.taskorganizer.datastructures.SettingWrapper;
import com.gharseldin.taskorganizer.datastructures.SettingsUnit;
import com.gharseldin.taskorganizer.datastructures.TaskRecord;
import com.gharseldin.taskorganizer.datastructures.UserRecord;

public class TaskListAdapter extends BaseAdapter {

	private Activity activity;
	private Context ctx;
	// private TaskRecord[] tasks;
	private UserRecord user;
	private ArrayList<SettingsUnit> profile;
	private ArrayList<TaskRecord> trackedTasks;
	private TaskDatabaseManager dbM;

	public TaskListAdapter(Activity activity,
			ArrayList<TaskRecord> trackedTasks, UserRecord user,
			ArrayList<SettingsUnit> profile) {
		this.activity = activity;
		this.trackedTasks = trackedTasks;
		this.ctx = activity;
		this.user = user;
		this.profile = profile;
		Log.d("From Adapter",
				"ArrayList Recieved with size = " + trackedTasks.size());
	}

	@Override
	public int getCount() {
		return trackedTasks.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		RelativeLayout container;
		ImageView taskImage;
		ImageView deleteTask;
		ImageView outline;
		CheckBox complete;
		TextView taskDescription;
		TextView dueDate;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		LayoutInflater inflater = activity.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.task, null);

			holder = new ViewHolder();

			holder.container = (RelativeLayout) convertView
					.findViewById(R.id.task_container);
			holder.taskImage = (ImageView) convertView
					.findViewById(R.id.task_image);
			holder.deleteTask = (ImageView) convertView
					.findViewById(R.id.delete_image);
			holder.outline = (ImageView) convertView.findViewById(R.id.outline);
			holder.complete = (CheckBox) convertView
					.findViewById(R.id.task_check);
			holder.taskDescription = (TextView) convertView
					.findViewById(R.id.task);
			holder.dueDate = (TextView) convertView.findViewById(R.id.due_date);

			convertView.setTag(holder); // reuse the inflated row (convertView)
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(trackedTasks.get(position).getPicture()!=null){
			String path = trackedTasks.get(position).getPicture();
			Drawable img = setUpImageView(path);
			if(img!=null){
				holder.taskImage.setImageDrawable(img);
			}else { }
		}else{ }
		
		holder.taskDescription.setText(trackedTasks.get(position).getContent());
		holder.dueDate.setText(trackedTasks.get(position).getDateDue());

		if (trackedTasks.get(position).getPriority().equals("High")) {
			holder.outline.setBackgroundResource(R.drawable.outlineimportant);
		}

		if (trackedTasks.get(position).getStatus().equals("complete")) {
			holder.complete.setChecked(true);
		} else {
			holder.complete.setChecked(false);
		}
		
		//setting onClickListeners for buttons

		holder.taskImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateTask(trackedTasks.get(position));

			}
		});

		holder.dueDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateTask(trackedTasks.get(position));

			}
		});

		holder.taskDescription.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateTask(trackedTasks.get(position));

			}
		});

		holder.complete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String status;
				if (holder.complete.isChecked()) {
					status = "complete";
				} else {
					status = "pending";
				}
				trackedTasks.get(position).setStatus(status);
				try {
					dbM = new TaskDatabaseManager(ctx);
					dbM.openDB();

					dbM.updateRecord(trackedTasks.get(position));
					dbM.closeDB();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		holder.deleteTask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Creating alert Dialog with two Buttons

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);

				// Setting Dialog Title
				alertDialog.setTitle("Confirm Delete...");

				// Setting Dialog Message
				alertDialog
						.setMessage("Are you sure you want to delete this Task?");

				// Setting Icon to Dialog
				alertDialog.setIcon(R.drawable.deleteicon);

				// Setting Positive "Yes" Button
				alertDialog.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Write your code here to execute after dialog
								try {
									dbM = new TaskDatabaseManager(ctx);
									dbM.openDB();

									dbM.deleteRecord(trackedTasks.get(position));
									dbM.closeDB();

								} catch (Exception e) {
									e.printStackTrace();
								}
								notifyDataSetInvalidated();
								notifyDataSetChanged();

								Toast.makeText(
										ctx,
										"Your task has been deleted successfully",
										Toast.LENGTH_SHORT).show();
								// TODO find a way to update the screen on
								// Delete
							}
						});
				// Setting Negative "NO" Button
				alertDialog.setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Write your code here to execute after dialog

								dialog.cancel();
							}
						});

				// Showing Alert Message
				alertDialog.show();

			}
		});

		return convertView;
	}

	private void updateTask(TaskRecord task) {

		// make an intent and wrap the user details, settings and Task to be
		// passed to UpdateActivity
		SettingWrapper wrap = new SettingWrapper(profile);
		Intent updateTaskIntent = new Intent(ctx, UpdateTaskActivity.class);
		Log.d("Adapter", user.getFirstName());
		updateTaskIntent.putExtra("com.gharseldin.taskorganize.user", user)
				.putExtra("com.gharseldin.taskorganize.settings", wrap)
				.putExtra("com.gharseldin.taskorganize.task", task);

		ctx.startActivity(updateTaskIntent);

	}

	private Drawable setUpImageView(String imagePath) {

		try {
			if (imagePath.length() != 0) {
				File f = new File(imagePath);
				if (f.exists()) {
					Bitmap image = shrinkBitmap(f.getPath(), 400, 400);
					Drawable imageDrawable = new BitmapDrawable(
							ctx.getResources(), image);
					return imageDrawable;
					// imageView.setBackgroundColor(Color.WHITE);
				} else {
					// if a path exists but a file does not then decide what to
					// view
					return null;
				}
			} else {
				// if there is no path for image decide what to view
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	/** Shrinking bitmap image to avoid outOfMemory exception */
	public Bitmap shrinkBitmap(String bitmapPath, int width, int height) {

		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath, bmpFactoryOptions);

		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) height);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) width);

		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}

		bmpFactoryOptions.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(bitmapPath, bmpFactoryOptions);
		return bitmap;
	}
}
