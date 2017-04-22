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

package com.octopus.wallet.u.a.mo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.octopus.wallet.R;
import com.octopus.wallet.m.a.FileGroupAdapter;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.e.NoFilesFound;
import com.octopus.wallet.m.p.fl.Dir;
import com.octopus.wallet.m.p.fl.Ext;
import com.octopus.wallet.m.p.fl.FGroup;
import com.octopus.wallet.m.p.fl.fmodels.FDeleteConfirmation;
import com.octopus.wallet.m.p.fl.fmodels.FRenameConfirmation;
import com.octopus.wallet.m.p.fl.fmodels.GroupedFilesData;
import com.octopus.wallet.m.tx.FLTrans;
import com.octopus.wallet.u.a.BActivity;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yoctopus.cac.notif.NDialog;
import me.yoctopus.cac.tx.Tx;
import me.yoctopus.cac.util.LogUtil;

public class ReportsActivity extends BActivity implements
        Tx.OnComplete<GroupedFilesData> {
    private final int LOAD_FILES = 1;
    private final int DELETE_FILES = 2;
    private final int RENAME_FILE = 3;
    private final int DELETE_FILE = 4;
    @InjectView(R.id.reportsView)
    RecyclerView reportsView;
    private Runnable initRunnable =
            new Runnable() {
                @Override
                public void run() {
                    loadReports();
                }
            };
    private FileGroupAdapter.onOptionsClickedListener
            onOptionsClickedListener
            = new FileGroupAdapter.onOptionsClickedListener() {
        @Override
        public void onShareClicked(
                File file,
                Ext ext) {
            shareFile(file,
                    ext);
        }

        @Override
        public void onRenameClicked(
                File file) {
            renameFile(file);
        }

        @Override
        public void onDeleteClicked(
                File file) {
            deleteFile(file);
        }

        @Override
        public void onViewClicked(
                File file,
                Ext ext) {
            viewFile(file,
                    ext);
        }

        @Override
        public void deleteClicked(
                Dir dir) {
            deleteFiles(dir);
        }

        @Override
        public void displayComplete() {

        }
    };

    @OnClick(R.id.newReportBtn)
    public void onViewClicked() {
        generateReport();
    }

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Reports And Receipts");
        setContentView(R.layout.activity_reports);
        ButterKnife.inject(this);
        setHasBackButton(true);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(
                        this);
        reportsView.setLayoutManager(
                layoutManager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        executeRunnable(initRunnable);
    }

    public void generateReport() {
        startActivity(
                getIntent(
                        ExportActivity.class));
    }

    private void loadReports() {
        transact(new LoadFiles(this, LOAD_FILES, Dir.Main),
                this);

    }

    private void listFiles(ArrayList<FGroup> files) {
        FileGroupAdapter adapter = new FileGroupAdapter(
                this,
                new SList<>(files));
        adapter.setClickedListener(
                onOptionsClickedListener);
        reportsView.setAdapter(adapter);
        dismissNotification();
    }

    private void viewFile(File file, Ext ext) {
        Intent viewIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.fromFile(file));
        Intent new_intent =
                Intent.createChooser(viewIntent,
                        "Open with ...");
        startActivity(new_intent);
    }

    private void deleteFile(final File file) {
        notifyDialog("Warning",
                "You are about do delete " +
                        file.getName(),
                new NDialog.DButton("Confirm",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                transact(
                                        new DeleteFile(ReportsActivity.this,
                                                DELETE_FILE,
                                                file),
                                        new Tx.OnComplete<FDeleteConfirmation>() {
                                            @Override
                                            public void onComplete(int id,
                                                                   FDeleteConfirmation
                                                                           simpleFileDeleteConfirmation) {
                                                getHandler().post(initRunnable);
                                            }
                                        });
                            }
                        }),
                null);

    }

    private void renameFile(final File file) {
        notifyDialog("Please enter the new file name without extension",
                null,
                new NDialog.DButton("OK",
                        null),
                null,
                new NDialog.OnAnswer() {
                    @Override
                    public void onAnswer(String answer) {
                        if (answer.isEmpty()) {
                            notifyToast("Empty response");
                            return;
                        }
                        transact(new RenameFile(
                                        ReportsActivity.this,
                                        100,
                                        file,
                                        answer),
                                new Tx.OnComplete<FRenameConfirmation>() {
                                    @Override
                                    public void onComplete(int id,
                                                           FRenameConfirmation
                                                                   simpleFileRenameConfirmation) {

                                        notifyToast("Renamed successfully");
                                        executeRunnable(initRunnable);
                                    }
                                }
                        );
                    }
                });
    }

    private void shareFile(File file,
                           Ext ext) {
        Uri uri = Uri.fromFile(file);
        Intent sharingIntent = new Intent(
                Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM,
                uri);
        sharingIntent.setData(uri);
        switch (ext.getExtension()) {
            case Ext.CS: {
                sharingIntent.setType(
                        "application/csv");
                break;
            }
            case Ext.PD: {
                sharingIntent.setType(
                        "application/pdf");
                break;
            }
            case Ext.XL: {
                sharingIntent.setType(
                        "application/xls");
                break;
            }
            case Ext.PN: {
                sharingIntent.setType(
                        "image/png");
                break;
            }
            case Ext.JP: {
                sharingIntent.setType(
                        "image/jpeg");
                break;
            }
        }
        Intent new_intent =
                Intent.createChooser(sharingIntent,
                        "Share with ...");
        startActivity(new_intent);
    }

    private void deleteFiles(final Dir dir) {
        notifyDialog("Warning",
                "You are about do delete multiple files",
                new NDialog.DButton("Confirm",
                        new NDialog.DButton.BListener() {
                            @Override
                            public void onClick(View v) {
                                transact(
                                        new DeleteAllFiles(
                                                ReportsActivity.this,
                                                DELETE_FILES,
                                                dir),
                                        new Tx.OnComplete<FDeleteConfirmation>() {
                                            @Override
                                            public void onComplete(int id,
                                                                   FDeleteConfirmation
                                                                           confirmation) {
                                                getHandler().post(initRunnable);
                                            }
                                        });
                            }
                        }),
                null);
    }

    @Override
    public void onComplete(int id,
                           GroupedFilesData data) {
        if (data == null) {
            notifyToast("Could not check files");
            return;
        }
        switch (id) {
            case LOAD_FILES: {
                if (!data.getfGroups().isEmpty()) {
                    listFiles((ArrayList<FGroup>)
                            data.getfGroups());
                } else {
                    notifyDialog("No reports available",
                            "Would you like " +
                                    "to generate one",
                            new NDialog.DButton(
                                    "Yes",
                                    new NDialog.DButton.BListener() {
                                        @Override
                                        public void onClick(View v) {
                                            generateReport();
                                        }
                                    }),
                            null);
                }
                break;
            }
            case DELETE_FILES:
            case RENAME_FILE: {
                getHandler().post(initRunnable);
                break;
            }
        }
    }

    public static class DeleteAllFiles extends
            FLTrans<FDeleteConfirmation> {
        private String TAG = LogUtil.makeTag(DeleteAllFiles.class);
        private Dir dir;
        public DeleteAllFiles(Context context,
                              int id,
                              Dir dir) {
            super(context,
                    id);
            this.dir = dir;
        }

        @Override
        public Progress<FDeleteConfirmation,
                        FDeleteConfirmation> getProgress() {
            return null;
        }

        @Override
        public CallBacks<FDeleteConfirmation,
                FDeleteConfirmation> getCallBacks() {
            return new CallBacks<FDeleteConfirmation,
                    FDeleteConfirmation>() {
                @Override
                public void onStart() {

                }

                @Override
                public FDeleteConfirmation onExecute() {
                    FDeleteConfirmation confirmation =
                            new FDeleteConfirmation
                                    ("delete_all_files");
                    confirmation.setDeleted(getFManager().deleteAll(
                            dir));
                    return confirmation;
                }

                @Override
                public void onProgress(FDeleteConfirmation... x) {

                }



                @Override
                public void onEnd(
                        FDeleteConfirmation simpleFileDeleteConfirmation) {
                    if (simpleFileDeleteConfirmation == null) {
                        return;
                    }
                    LogUtil.d(TAG,
                            "endTransaction: "+
                                    simpleFileDeleteConfirmation.getNameOfData());

                }
            };
        }
    }

    /**
     * Created by octopus on 10/25/16.
     */
    public static class DeleteFile extends
            FLTrans<FDeleteConfirmation> {
        private String TAG = LogUtil.makeTag(DeleteFile.class);
        private File file;
        public DeleteFile(Context context,
                          int id,
                          File file) {
            super(context,
                    id);
            this.file = file;
        }

        @Override
        public Progress<FDeleteConfirmation,
                        FDeleteConfirmation> getProgress() {
            return null;
        }

        @Override
        public CallBacks<FDeleteConfirmation,
                FDeleteConfirmation> getCallBacks() {
            return new CallBacks<FDeleteConfirmation,
                    FDeleteConfirmation>() {
                @Override
                public void onStart() {
                    LogUtil.d(TAG,
                            "beforeExecuting: ");
                }

                @Override
                public FDeleteConfirmation onExecute() {
                    LogUtil.i(TAG,
                            "execute: ");
                    FDeleteConfirmation confirmation =
                            new FDeleteConfirmation(
                                    "delete_confirmed");
                    confirmation.setDeleted(getFManager().delete(
                            file));
                    return confirmation;
                }

                @Override
                public void onProgress(FDeleteConfirmation... x) {

                }



                @Override
                public void onEnd(
                        FDeleteConfirmation simpleFileDeleteConfirmation) {
                    if (simpleFileDeleteConfirmation == null) {
                        return;
                    }
                    LogUtil.d(TAG,
                            "endTransaction: "+
                                    simpleFileDeleteConfirmation.getNameOfData());

                }
            };
        }
    }

    public static class LoadFiles extends FLTrans<GroupedFilesData> {
        private String TAG = LogUtil.makeTag(LoadFiles.class);
        private Dir dir;
        public LoadFiles(Context context, int id, Dir dir) {
            super(context, id);
            this.dir = dir;

        }

        @Override
        public Progress<GroupedFilesData, GroupedFilesData> getProgress() {
            return null;
        }

        @Override
        public CallBacks<GroupedFilesData,
                GroupedFilesData> getCallBacks() {
            return new CallBacks<GroupedFilesData,
                    GroupedFilesData>() {
                @Override
                public void onStart() {
                    LogUtil.i(TAG,
                            "beforeExecuting: ");

                }

                @Override
                public GroupedFilesData onExecute() {
                    LogUtil.i(TAG,
                            "execute: ");
                    GroupedFilesData filesData =
                            new GroupedFilesData("imported_groups");
                    try {
                        ArrayList<File> files =
                                (ArrayList<File>) getFManager().importFiles(dir);
                        filesData.setfGroups(getFManager().categorise(files));
                    } catch (NoFilesFound e) {
                        LogUtil.e(TAG,
                                "execute: ",
                                e);
                        return null;
                    }
                    return filesData;
                }

                @Override
                public void onProgress(GroupedFilesData... x) {

                }



                @Override
                public void onEnd(
                        GroupedFilesData groupedFilesData) {

                }
            };
        }
    }

    public static class RenameFile extends
            FLTrans<FRenameConfirmation> {
        private String TAG = LogUtil.makeTag(RenameFile.class);
        private File file;
        private String newName;
        public RenameFile(Context context,
                          int id,
                          File file,
                          String newName) {
            super(context,
                    id);
            this.file = file;
            this.newName = newName;
        }

        @Override
        public Progress<FRenameConfirmation, FRenameConfirmation> getProgress() {
            return null;
        }

        @Override
        public CallBacks<FRenameConfirmation,
                FRenameConfirmation> getCallBacks() {
            return new CallBacks<FRenameConfirmation,
                    FRenameConfirmation>() {
                @Override
                public void onStart() {
                    LogUtil.d(TAG,
                            "beforeExecuting: ");
                }

                @Override
                public FRenameConfirmation onExecute() {
                    FRenameConfirmation confirmation =
                            new FRenameConfirmation(
                                    "rename_file");
                    confirmation.setRenamed(getFManager().rename(
                            file,
                            newName));
                    return confirmation;
                }

                @Override
                public void onProgress(FRenameConfirmation... x) {

                }



                @Override
                public void onEnd(FRenameConfirmation simpleFileRenameConfirmation) {
                    if (simpleFileRenameConfirmation == null) {
                        return;
                    }
                    LogUtil.d(TAG,
                            "endTransaction: "+
                                    simpleFileRenameConfirmation.getNameOfData());

                }
            };
        }
    }

}
