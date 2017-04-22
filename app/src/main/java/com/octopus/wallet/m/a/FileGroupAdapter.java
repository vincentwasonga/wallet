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

package com.octopus.wallet.m.a;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.octopus.wallet.R;
import com.octopus.wallet.m.b.SList;
import com.octopus.wallet.m.b.VBinder;
import com.octopus.wallet.m.p.fl.Dir;
import com.octopus.wallet.m.p.fl.Ext;
import com.octopus.wallet.m.p.fl.FGroup;
import com.octopus.wallet.m.p.fl.SFile;
import com.octopus.wallet.m.v.a.ListAnimator;

import java.io.File;

import me.yoctopus.cac.anim.Animator;

/**
 * Created by octopus on 10/24/16.
 */
public class FileGroupAdapter extends VBinder<FGroup> {
    private TextView groupName;
    private RecyclerView filesView;
    private Button deleteFilesBtn;
    private onOptionsClickedListener clickedListener;
    private Context context;
    private ImageLoader loader = ImageLoader.getInstance();

    public FileGroupAdapter(Context context,
                            SList<FGroup> fGroups) {
        super(fGroups,
                R.layout.file_group_item_row);
        this.context = context;
        ImageLoaderConfiguration configuration =
                ImageLoaderConfiguration.createDefault(context);
        loader.init(configuration);
    }

    private String getShortDirectoryName(Dir dir) {
        String[] parts = dir.getName()
                .split(File.separator);
        if (parts.length == 1) {
            return parts[0];
        } else {
            return parts[parts.length - 1];
        }
    }




    @Override
    public void onInit(View itemView) {
        groupName = (TextView) itemView
                .findViewById(R.id.directoryName);
        filesView = (RecyclerView) itemView
                .findViewById(R.id.fileList);
        filesView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = null;
        linearLayoutManager =
                new LinearLayoutManager(context);
        filesView.setLayoutManager(linearLayoutManager);
        filesView.addItemDecoration(new AdapterDivider(context,
                LinearLayout.VERTICAL));
        filesView.setItemAnimator(
                new DefaultItemAnimator());
        deleteFilesBtn = (Button) itemView
                .findViewById(R.id.deleteButton);
    }

    @Override
    public void onBind(FGroup fGroup) {
        groupName.setText(getShortDirectoryName(
                fGroup.getDir()));
        FilesAdapter adapter = new FilesAdapter(
                new SList<>(fGroup.getFiles()));
        filesView.setAdapter(adapter);
        if (!fGroup.hasFiles()) {
            deleteFilesBtn.setVisibility(
                    View.GONE);
            groupName.setVisibility(
                    View.GONE);
            return;
        }
        if (fGroup.getDir().getName()
                .equals(Dir.Receipts.getName())) {
            deleteFilesBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickedListener.deleteClicked(
                                    Dir.Receipts);
                        }
                    });
        } else if (fGroup.getDir().getName()
                .equals(Dir.Reports.getName())) {
            deleteFilesBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickedListener.deleteClicked(
                                    Dir.Reports);
                        }
                    });
        } else if (fGroup.getDir().getName()
                .equals(Dir.Camera.getName())) {
            deleteFilesBtn.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickedListener.deleteClicked(
                                    Dir.Camera);
                        }
                    });
        }
        clickedListener
                .displayComplete();
    }

    private class FilesAdapter extends VBinder<SFile> {
        ImageView fileIcon;
        TextView fileName;
        ImageButton shareButton;
        ImageButton renameButton;
        ImageButton deleteButton;


        FilesAdapter(SList<SFile> files) {
            super(files, R.layout.file_item);
        }

        private String getSimpleFileName(File file) {
            String name = file.getName();
            if (name.contains(".")) {
                String[] names = name.split(".");
                if (names.length == 2) {
                    return names[0];
                }
            }

            return name;
        }


        @Override
        public void onInit(View itemView) {
            fileIcon = (ImageView) itemView
                    .findViewById(R.id.fileIcon);
            fileName = (TextView) itemView
                    .findViewById(R.id.fileName);
            shareButton = (ImageButton) itemView
                    .findViewById(R.id.shareButton);
            renameButton = (ImageButton) itemView
                    .findViewById(R.id.renameButton);
            deleteButton = (ImageButton) itemView
                    .findViewById(R.id.deleteButton);
            Animator animator = new ListAnimator(
                    itemView);
            animator.animate();
        }

        @Override
        public void onBind(final SFile file) {
            fileName.setText(
                    getSimpleFileName(file.getFile()));

            if (file.getFile().getName().contains(
                    Ext.CSV.getExtension())) {
                View.OnClickListener listener =
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onViewClicked(file.getFile(),
                                        Ext.CSV);
                            }
                        };
                fileIcon.setImageDrawable(TextDrawable.builder()
                        .beginConfig()
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRound("C",
                                ColorGenerator.MATERIAL.getRandomColor()));

                fileIcon.setOnClickListener(listener);
                fileName.setOnClickListener(listener);
                shareButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onShareClicked(file.getFile(),
                                        Ext.CSV);
                            }
                        });
            }
            if (file.getFile().getName().contains(
                    Ext.PDF.getExtension())) {
                View.OnClickListener listener =
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onViewClicked(file.getFile(),
                                        Ext.PDF);
                            }
                        };
                fileIcon.setImageDrawable(TextDrawable.builder()
                        .beginConfig()
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRound("P",
                                ColorGenerator.MATERIAL.getRandomColor()));
                fileIcon.setOnClickListener(listener);
                fileName.setOnClickListener(listener);
                shareButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onShareClicked(file.getFile(),
                                        Ext.PDF);
                            }
                        });
            }
            if (file.getFile().getName().contains(
                    Ext.XLS.getExtension())) {
                View.OnClickListener listener =
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onViewClicked(file.getFile(),
                                        Ext.XLS);
                            }
                        };
                fileIcon.setImageDrawable(TextDrawable.builder()
                        .beginConfig()
                        .bold()
                        .toUpperCase()
                        .endConfig()
                        .buildRound("X",
                                ColorGenerator.MATERIAL.getRandomColor()));
                fileIcon.setOnClickListener(listener);
                fileName.setOnClickListener(listener);
                shareButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onShareClicked(file.getFile(),
                                        Ext.PDF);
                            }
                        });
            }
            if (file.getFile().getName().contains(
                    Ext.PNG.getExtension())) {
                View.OnClickListener listener =
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onViewClicked(file.getFile(),
                                        Ext.PNG);
                            }
                        };
                loader.displayImage("file://"+file.getFile().getAbsolutePath(),
                        fileIcon);

                fileIcon.setOnClickListener(listener);
                fileName.setOnClickListener(listener);
                shareButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onShareClicked(file.getFile(),
                                        Ext.PNG);
                            }
                        });
            }
            if (file.getFile().getName().contains(
                    Ext.JPEG.getExtension())) {
                View.OnClickListener listener =
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onViewClicked(file.getFile(),
                                        Ext.PNG);
                            }
                        };
                loader.displayImage("file://"+file.getFile().getAbsolutePath(),
                        fileIcon);
                fileIcon.setOnClickListener(listener);
                fileName.setOnClickListener(listener);
                shareButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedListener.onShareClicked(file.getFile(),
                                        Ext.JPEG);
                            }
                        });
            }
            renameButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickedListener.onRenameClicked(file.getFile());
                        }
                    });
            deleteButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clickedListener.onDeleteClicked(file.getFile());
                        }
                    });
        }
    }

    public interface onOptionsClickedListener {
        void onShareClicked(File file,
                            Ext ext);

        void onRenameClicked(File file);

        void onDeleteClicked(File file);

        void onViewClicked(File file,
                           Ext ext);

        void deleteClicked(Dir dir);
        void displayComplete();
    }

    public onOptionsClickedListener getClickedListener() {
        return clickedListener;
    }

    public void setClickedListener(
            onOptionsClickedListener clickedListener) {
        this.clickedListener = clickedListener;
    }
}
