package org.yuhanxun.imageanalyzer.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.yuhanxun.imageanalyzer.app.R;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

//    Main main;

        final String SAMPLE_UNLABEL = "sample2w8";
//    final String SAMPLE_UNLABEL = "sample_unlabel";
    final String SAMPLE_LABELLED = "sample_label";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String emmcPath = Environment.getExternalStorageDirectory().getPath();

        String extSdcardPath = getSecondaryStoragePath(this);

        String parentPath = extSdcardPath;

//        main = new Main(this, parentPath + File.separator + SAMPLE_UNLABEL,
//                parentPath + File.separator + SAMPLE_LABELLED);
//        main.init();
//
//        new Handler().postDelayed(() -> main.start(), 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        main.destroy();
    }


    public String getSecondaryStoragePath(Context context) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", (Class<?>) null);
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, (Object) null);
            // second element in paths[] is secondary storage path
            return paths[1];
        } catch (Exception e) {
            Log.e("TAG", "getSecondaryStoragePath() failed", e);
        }
        return null;
    }
}