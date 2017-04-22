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
import org.opencv.core.Point;
import org.opencv.core.Size;

/**
 * Created by allgood on 05/03/16.
 */
public class ScannedReceipt {

    public Mat original;
    public Mat processed;
    public Quadrilateral quadrilateral;
    public Point[] previewPoints;
    public Size previewSize;

    public ScannedReceipt(Mat original) {
        this.original = original;
    }



    public ScannedReceipt setProcessed(Mat processed) {
        this.processed = processed;
        return this;
    }

    public void release() {
        if (processed != null) {
            processed.release();
        }
        if (original != null) {
            original.release();
        }

        if (quadrilateral != null &&
                quadrilateral.contour != null) {
            quadrilateral.contour.release();
        }
    }
}
