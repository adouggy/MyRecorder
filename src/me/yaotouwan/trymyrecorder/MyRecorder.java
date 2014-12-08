package me.yaotouwan.trymyrecorder;

import java.util.List;

import me.yaotouwan.trymyrecorder.ShellUtils.CommandResult;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;

import com.google.common.base.Splitter;

/**
 * encapsulate KitKat screenrecord command
 * ADB has the privilege: AID_GRAPHICS, which normal application doesn't have.
 * So, we need root!
 * 
 * @author liyazi
 */
public class MyRecorder {
	public final static String SYS_RECORD_COMMAND = "/system/bin/screenrecord";
	private final static Splitter blankSplitter = Splitter.on(" ").trimResults().omitEmptyStrings();

	public static void startRecording(String fileName) {
		final String command = SYS_RECORD_COMMAND + " " + fileName + " ";
		new Thread() {
			@Override
			public void run() {
				CommandResult res = ShellUtils.execCommand( command, true);
				Log.d("DEBUG", "res:" + res.errorMsg + "|" + res.successMsg + "|" + res.result);
			};
		}.start();
	}

	public static void stopRecording() {
		CommandResult res = ShellUtils.execCommand("ps | grep " + SYS_RECORD_COMMAND, false);
		Log.d("DEBUG", "res:" + res.errorMsg + "|" + res.successMsg + "|" + res.result);
		if (StringUtils.isNotEmpty(res.successMsg)) {
			List<String> list = blankSplitter.splitToList(res.successMsg);
			if (list != null && list.size() >= 2) {
				String pidStr = list.get(1);
				Log.d("DEBUG", "pidStr=" + pidStr);
				res = ShellUtils.execCommand("kill -2 " + pidStr, true);
				Log.d("DEBUG", "res:" + res.errorMsg + "|" + res.successMsg + "|" + res.result);
			}
		}
	}

}
