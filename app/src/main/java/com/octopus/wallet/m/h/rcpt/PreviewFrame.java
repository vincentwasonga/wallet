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

package com.octopus.wallet.m.h.rcpt;

import org.opencv.core.Mat;

/**
 * Created by allgood on 06/03/16.
 */
public class PreviewFrame {

    private final boolean previewOnly;
    private Mat frame;
    private boolean autoMode;

    public PreviewFrame( Mat frame , boolean autoMode , boolean previewOnly ) {
        this.frame = frame;
        this.autoMode = autoMode;
        this.previewOnly = previewOnly;
    }

    public Mat getFrame() {
        return frame;
    }



    public boolean isAutoMode() {
        return autoMode;
    }

    public boolean isPreviewOnly() {
        return previewOnly;
    }


}
