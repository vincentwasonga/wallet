/*
 * Copyright 2017, Peter Vincent
 * Licensed under the Apache License, Version 2.0, Vin Budget.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.octopus.wallet.m.p.fl;

import android.content.Context;

import com.octopus.wallet.m.e.InvalidExportChoice;
import com.octopus.wallet.m.e.NoFilesFound;
import com.octopus.wallet.m.p.fl.expoimpo.exp.ReportExporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.yoctopus.cac.util.LogUtil;


public class FManager {
    private String TAG = LogUtil.makeTag(FManager.class);
    private FileOperations fileOperations;
    private List<FGroup> fGroups;
    private File source,
            destination;
    private Dir dir;
    private Context context;

    public FManager(Context context) {
        this.context = context;
    }
    public List<File> importFiles(Dir dir) throws NoFilesFound {
        LogUtil.i(TAG,
                "importFiles: ");
        this.fGroups =
                new ArrayList<>();
        ArrayList<File> files =
                new ArrayList<>();
        File folder = new File(dir.getName());
        if (folder.isDirectory()) {
            File[] lists = folder.listFiles();
            if (lists == null || lists.length == 0) {
                throw new NoFilesFound();
            }
            for (File file : lists) {
                if (file.isDirectory()) {
                    files.addAll(Arrays.asList(file.listFiles()));
                }
                if (file.isFile()) {
                    files.add(file);
                }
            }
        } else {
            throw new NoFilesFound();
        }
        return files;
    }
    public boolean delete(String fileName) {
        LogUtil.i(TAG,
                "deleteFile: ");
        File file = new File(
                fileName);
        return file.delete();
    }
    public boolean delete(File file) {
        LogUtil.i(TAG,
                "onDelete: ");
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
    public boolean deleteAll(Dir dir) {
        LogUtil.i(TAG,
                "deleteFiles: ");
        File file =
                new File(dir.getName());
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                file1.delete();
            }
        }
        return true;
    }
    public boolean copy(File source,
                        File destination) {
        this.source = source;
        LogUtil.i(TAG,
                "copy: ");
        fileOperations =
                new FileOperations(source,
                        destination);
        this.destination =
                fileOperations.getDestination();
        return fileOperations.copy();
    }
    public boolean rename(File file,
                          String name) {
        LogUtil.i(TAG,
                "rename: ");
        FUri fUri =
                new FUri(dir,
                        name);
        File file1 =
                new File(
                        fUri.getUri().toString());
        fileOperations =
                new FileOperations(file,
                        file1);
        if (fileOperations.copy()) {
            file.delete();
            return true;
        }
        else {
            return false;
        }
    }
    public List<FGroup> categorise(ArrayList<File> files) {
        LogUtil.i(TAG,
                "categorise: ");
        this.fGroups = new ArrayList<>();
        FGroup receipts = new FGroup(Dir.Receipts);
        FGroup reports = new FGroup(Dir.Reports);
        FGroup camera = new FGroup(Dir.Camera);
        int key = 0;
        for (File file : files) {
            if (file.getParent().contains(
                    receipts.getShortDirectoryName())) {
                receipts.getFiles().add(new SFile(key, file));
            }
            else if (file.getParent().contains(
                    reports.getShortDirectoryName())) {
                reports.getFiles().add(new SFile(key, file));
            }
            else if (file.getParent().contains(
                    camera.getShortDirectoryName())) {
                camera.getFiles().add(new SFile(key, file));
            }
            key ++;
        }
        fGroups.add(camera);
        fGroups.add(reports);
        fGroups.add(receipts);
        return fGroups;
    }
    public boolean saveData(ReportExporter reportExporter) {
        LogUtil.d(TAG,
                "saveData: ");
        try {
            return reportExporter.export();
        } catch (InvalidExportChoice invalidExportChoice) {
            invalidExportChoice.printStackTrace();
            LogUtil.e(TAG,
                    "saveData: ",
                    invalidExportChoice);
            return false;
        }
    }

    public File getSource() {
        return source;
    }

    public void setSource(File source) {
        this.source = source;
    }

    public File getDestination() {
        return destination;
    }



    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    private static class FileOperations {
        private String TAG = LogUtil.makeTag(FileOperations.class);
        private File source,
                destination;
        private Dir dir;

        FileOperations(File source,
                       File destination) {
            this.source = source;
            this.destination = destination;
        }



        protected boolean copy() {
            LogUtil.i(TAG,
                    "copy: copying");
            if (destination.exists()) {
                try {
                    InputStream in =
                            new FileInputStream(source);
                    OutputStream out =
                            new FileOutputStream(destination);
                    byte[] buffer =
                            new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) >
                            0) {
                        out.write(buffer,
                                0,
                                read);
                    }
                    out.flush();
                    in.close();
                    out.close();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.i(TAG,
                            "copy: failed");
                    return false;
                }
            }
            LogUtil.i(TAG,
                    "copy: success");
            return true;
        }

        protected Dir getDir() {
            return dir;
        }

        protected void setDir(Dir dir) {
            this.dir = dir;
        }

        protected File getSource() {
            return source;
        }

        protected void setSource(File source) {
            this.source = source;
        }

        protected File getDestination() {
            return destination;
        }


    }
}
