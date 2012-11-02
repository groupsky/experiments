package geno.roupsky.nativecrashreporting;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	protected static final String TAG = null;
	private TextView resultView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn_bug_report).setOnClickListener(this);
		findViewById(R.id.btn_app_error).setOnClickListener(this);
		resultView = (TextView) findViewById(R.id.tx_result);
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.btn_bug_report:
				doBugReport();
				break;
			case R.id.btn_app_error:
				doAppError();
				break;
			}
		} catch (Exception e) {
			log("unhandled exception: " + e.getMessage());
		}
	}

	@SuppressLint("NewApi")
	private void doAppError() {
		log("starting app error");

		Exception e = new RuntimeException("test exception");

		ApplicationErrorReport report = new ApplicationErrorReport();
		report.packageName = report.processName = getApplication()
				.getPackageName();
		report.time = System.currentTimeMillis();
		report.type = ApplicationErrorReport.TYPE_CRASH;
		report.systemApp = false;

		ApplicationErrorReport.CrashInfo crash = new ApplicationErrorReport.CrashInfo();
		crash.exceptionClassName = e.getClass().getSimpleName();
		crash.exceptionMessage = e.getMessage();

		StringWriter writer = new StringWriter();
		PrintWriter printer = new PrintWriter(writer);
		e.printStackTrace(printer);

		crash.stackTrace = writer.toString();

		StackTraceElement stack = e.getStackTrace()[0];
		crash.throwClassName = stack.getClassName();
		crash.throwFileName = stack.getFileName();
		crash.throwLineNumber = stack.getLineNumber();
		crash.throwMethodName = stack.getMethodName();

		report.crashInfo = crash;

		Intent intent = new Intent(Intent.ACTION_APP_ERROR);
		intent.putExtra(Intent.EXTRA_BUG_REPORT, report);
		startActivity(intent);
	}

	private void doBugReport() {
		log("starting bug report");
		Intent intent = new Intent(Intent.ACTION_BUG_REPORT);
		ServiceConnection service = new ServiceConnection() {
			public void onServiceConnected(ComponentName componentName,
					IBinder binder) {
				try {
					Parcel parcel = Parcel.obtain();
					if (binder.transact(1, parcel, null, 0))
						log("transact ok");
					else
						log("transact err");
					return;
				} catch (RemoteException e) {
					Log.d(TAG, e.getMessage(), e);
					log("exception while transacting: " + e.getMessage());
				}
			}

			public void onServiceDisconnected(ComponentName componentName) {
			}
		};
		if (!bindService(intent, service, Context.BIND_AUTO_CREATE))
			log("can't bind service");
	}

	private void log(String message) {
		resultView.append("\n");
		resultView.append(message);
	}
}
