package com.ruixinhua.mycoursetimetable;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String id = intent.getStringExtra("id");
		int position = intent.getIntExtra("position", -1);
		//Log.i("position", position+"");
		ModuleService ms = new ModuleService(context);
		Module module = ms.getObject(id);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = new Notification.Builder(context);

        mBuilder.setContentTitle(module.getModule_name()+" at "+module.getLocation())
                .setContentText("There is "+module.getNotification()+" minutes left")
                .setSmallIcon(R.drawable.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND);
        notificationManager.notify(position, mBuilder.build());
        //Log.i("notification", module.getStart_end_time());
		Toast.makeText(context, module.getModule_name()+" at "+module.getLocation()+"\nThere is "+module.getNotification()+" minutes left", Toast.LENGTH_SHORT)
		.show();
	}

}
