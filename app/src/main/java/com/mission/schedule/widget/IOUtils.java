package com.mission.schedule.widget;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 */
public class IOUtils {
    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile() && file.delete();
    }
}
