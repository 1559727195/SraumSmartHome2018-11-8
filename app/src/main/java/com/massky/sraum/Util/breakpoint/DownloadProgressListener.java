package com.massky.sraum.Util.breakpoint;

import java.io.File;

/**
 * Created by zhu on 2018/9/7.
 */

public interface DownloadProgressListener {
    void onDownloadSize(int downloadedSize, File file);

    void onDownLoadError();
}