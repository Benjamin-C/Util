package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	private PrintStream logPrintStream;
	private PrintStream tracedPrintStream;
	private PrintStream outputPrintStream;
	
	private PrintStream defaultSystemOut;
	private PrintStream defaultSystemErr;
	
	private boolean paused;
	private boolean active;
	
	private boolean traced;
	private String nameEnd;
	
	public Logger() {
		this(true, "");
	}
	public Logger(boolean traced, String nameEnd) {
		defaultSystemOut = System.out;
		defaultSystemErr = System.err;
		this.traced = traced;
		this.nameEnd = nameEnd;
	}
	
	public void start() {
		if(!active) {
			active = true;
			DateFormat logPath = new SimpleDateFormat("yyyy/MM/dd");
	 		DateFormat logName = new SimpleDateFormat("yyyyMMddHHmmss");
	 		Date cal = new Date();
	 		String logPathStr = "logs/" + logPath.format(cal);
	 		String logNameStr = logName.format(cal) + nameEnd + ".txt";
	 		System.out.println("logname =" + logPathStr  + "/" + logNameStr);
	 		File f = new File(logPathStr);
	 		f.mkdirs();
	 		try {
				logPrintStream = new PrintStream(new PrintStream(logPathStr + "/" + logNameStr), true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 		outputPrintStream = new PrintStreamDuplicator(logPrintStream, System.out);
	 		if(traced) {
	 			tracedPrintStream = new TracedPrintStream(outputPrintStream, true);
	 		} else {
	 			tracedPrintStream = outputPrintStream;
	 		}
	 		System.setOut(tracedPrintStream);
	 		System.setErr(outputPrintStream);
	 		logPrintStream.flush();
		}
	}
	
	public void pause() {
		if(active && !paused) {
			paused = true;
			System.setOut(defaultSystemOut);
	 		System.setErr(defaultSystemErr);
		}
	}
	
	public void resume() {
		if(paused) {
			System.setOut(tracedPrintStream);
	 		System.setErr(outputPrintStream);
		}
	}
	
	public void stop() {
		if(active) {
			active = false;
			logPrintStream.close();
			if(!paused) {
				System.setOut(defaultSystemOut);
		 		System.setErr(defaultSystemErr);
			}
		}
	}
}
