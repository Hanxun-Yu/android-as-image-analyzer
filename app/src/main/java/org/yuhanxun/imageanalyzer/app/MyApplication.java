package org.yuhanxun.imageanalyzer.app;

import android.app.Application;
import android.os.Process;

import org.yuhanxun.libcommonutil.log.FileLogFactory;
import org.yuhanxun.libcommonutil.log.ILog;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
