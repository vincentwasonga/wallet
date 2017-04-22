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

package com.octopus.wallet.m.h.utl;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.PathShape;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.octopus.wallet.m.h.rcpt.PreviewFrame;
import com.octopus.wallet.m.h.rcpt.Quadrilateral;
import com.octopus.wallet.m.h.rcpt.ReceiptMessage;
import com.octopus.wallet.m.h.rcpt.ScannedReceipt;
import com.octopus.wallet.m.v.w.CanvasView;
import com.octopus.wallet.u.a.t.ReceiptActivity;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import me.yoctopus.cac.util.LogUtil;

/**
 * Created by yoctopus on 11/12/16.
 */

public class BitmapUtil {




    public static Bitmap getBitmap(
            @NonNull Context context,
                                   @DrawableRes int id) {

        return BitmapFactory.decodeResource(
                context.getResources(),
                id);
    }







    public static Bitmap decodeSampledBitmapFromUri(String path,
                                                    int reqWidth,
                                                    int reqHeight) {

        Bitmap bm;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options =
                new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,
                options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,
                reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize update
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path,
                options);

        return bm;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth,
                                            int reqHeight) {

        // Raw height add width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight ||
                width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public static void addImageToGallery(final String filePath,
                                         final Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN,
                System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE,
                "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA,
                filePath);
        context.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
    }

    public static class ReceiptProcessor extends Handler {
        private static final String TAG =
                LogUtil.makeTag(
                        ReceiptProcessor.class);
        private final ReceiptActivity.ReceiptProcessorListener
                processorListener;

        private boolean colorMode =
                false;
        private boolean filterMode =
                true;
        private double colorGain =
                1.5;       // contrast
        private double colorBias =
                0;         // bright
        private int colorThresh =
                110;        // threshold
        private Size previewSize;
        private Point[] previewPoints;
        private HashMap<String, Long> pageHistory =
                new HashMap<>();
        private QRCodeMultiReader qrCodeMultiReader =
                new QRCodeMultiReader();

        public ReceiptProcessor(Looper looper,
                                ReceiptActivity.ReceiptProcessorListener
                                        processorListener) {
            super(looper);
            this.processorListener = processorListener;
        }

        public void handleMessage(Message msg) {
            if (msg.obj.getClass() == ReceiptMessage.class) {
                ReceiptMessage obj = (ReceiptMessage) msg.obj;
                String command = obj.getCommand();
                LogUtil.d(TAG,
                        "Message Received: " +
                                command +
                                " - " +
                                obj.getObj().toString());
                switch (command) {
                    case "previewFrame":
                        processPreviewFrame((PreviewFrame) obj.getObj());
                        break;
                    case "pictureTaken":
                        processPicture((Mat) obj.getObj());
                        break;
                    case "colorMode":
                        colorMode = (boolean) obj.getObj();
                        break;
                    case "filterMode":
                        filterMode = (boolean) obj.getObj();
                        break;

                }
            }
        }

        private void processPreviewFrame(PreviewFrame previewFrame) {
            Result[] results = {};
            Mat frame = previewFrame.getFrame();
            try {
                results = zxing(frame);
            } catch (ChecksumException |
                    FormatException e) {
                e.printStackTrace();
            }
            boolean qrOk = false;
            String currentQR = null;
            for (Result result : results) {
                String qrText = result.getText();
                if (Func.isMatch(qrText,
                        "^P.. V.. S[0-9]+") &&
                        checkQR(qrText)) {
                    LogUtil.d(TAG,
                            "QR Code valid: " +
                                    result.getText());
                    qrOk = true;
                    currentQR = qrText;
                    break;
                } else {
                    LogUtil.d(TAG,
                            "QR Code ignored: " +
                                    result.getText());
                }
            }
            boolean autoMode = previewFrame.isAutoMode();
            boolean previewOnly = previewFrame.isPreviewOnly();
            if (detectPreviewDocument(frame) &&
                    ((!autoMode &&
                            !previewOnly) ||
                            (autoMode &&
                                    qrOk))) {
                processorListener.waitProgressVisible();
                processorListener.requestPicture();
                if (qrOk) {
                    pageHistory.put(currentQR,
                            new Date().getTime() /
                                    1000);
                    LogUtil.d(TAG,
                            "QR Code scanned: " +
                                    currentQR);
                }
            }
            frame.release();
            processorListener.setImageProcessorBusy(false);
        }

        void processPicture(Mat picture) {
            Mat img = Imgcodecs.imdecode(picture,
                    Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
            picture.release();
            LogUtil.d(TAG,
                    "processPicture -" +
                            " imported image " +
                            img.size().width +
                            "x" +
                            img.size().height);

            ScannedReceipt doc = detectDocument(img);
            processorListener.saveDocument(doc);
            doc.release();
            picture.release();
            processorListener.setImageProcessorBusy(
                    false);
            processorListener.waitProgressInvisible();
        }

        private ScannedReceipt detectDocument(Mat inputRgba) {
            ArrayList<MatOfPoint> contours = findContours(inputRgba);
            ScannedReceipt sd = new ScannedReceipt(inputRgba);
            Quadrilateral quad = getQuadrilateral(contours,
                    inputRgba.size());
            Mat doc;
            if (quad != null) {
                sd.quadrilateral = quad;
                sd.previewPoints = previewPoints;
                sd.previewSize = previewSize;
                doc = fourPointTransform(inputRgba,
                        quad.points);
            } else {
                doc = new Mat(inputRgba.size(),
                        CvType.CV_8UC4);
                inputRgba.copyTo(doc);
            }
            enhanceDocument(doc);
            return sd.setProcessed(doc);
        }

        private boolean checkQR(String qrCode) {
            return !(pageHistory.containsKey(qrCode) &&
                    pageHistory.get(qrCode) >
                            new Date().getTime() /
                                    1000 -
                                    15);
        }

        private boolean detectPreviewDocument(
                Mat inputRgba) {
            ArrayList<MatOfPoint> contours =
                    findContours(inputRgba);
            Quadrilateral quad =
                    getQuadrilateral(contours,
                    inputRgba.size());
            previewPoints = null;
            previewSize = inputRgba.size();
            if (quad != null) {
                Point[] rescaledPoints =
                        new Point[4];
                double ratio = inputRgba.size().height /
                        500;
                for (int i = 0; i < 4; i++) {
                    int x = Double.valueOf(quad.points[i].x *
                            ratio).intValue();
                    int y = Double.valueOf(quad.points[i].y *
                            ratio).intValue();
                    rescaledPoints[i] =
                            new Point(x,
                                    y);
                }
                previewPoints = rescaledPoints;
                drawDocumentBox(previewPoints,
                        previewSize);
                LogUtil.d(TAG,
                        quad.points[0].toString() +
                                " , " +
                                quad.points[1].toString() +
                                " , " + quad.points[2].toString() +
                                " , " + quad.points[3].toString());
                return true;
            }
            processorListener.getCanvasView().clear();
            processorListener.invalidateHUD();
            return false;
        }

        private void drawDocumentBox(Point[] points,
                                     Size stdSize) {
            Path path = new Path();
            CanvasView hud =
                    processorListener.getCanvasView();
            // ATTENTION: axis are swapped
            float previewWidth = (float) stdSize.height;
            float previewHeight = (float) stdSize.width;
            path.moveTo(previewWidth -
                    (float) points[0].y,
                    (float) points[0].x);
            path.lineTo(previewWidth -
                    (float) points[1].y,
                    (float) points[1].x);
            path.lineTo(previewWidth -
                    (float) points[2].y,
                    (float) points[2].x);
            path.lineTo(previewWidth -
                    (float) points[3].y,
                    (float) points[3].x);
            path.close();
            PathShape newBox =
                    new PathShape(path,
                    previewWidth,
                    previewHeight);
            Paint paint =
                    new Paint();
            paint.setColor(Color.argb(64,
                    0,
                    255,
                    0));
            Paint border =
                    new Paint();
            border.setColor(Color.rgb(0,
                    255,
                    0));
            border.setStrokeWidth(5);
            hud.clear();
            hud.addShape(newBox,
                    paint,
                    border);
            processorListener.invalidateHUD();
        }

        private Quadrilateral getQuadrilateral(ArrayList<MatOfPoint> contours,
                                               Size srcSize) {

            double ratio = srcSize.height /
                    500;
            int height = Double.valueOf(srcSize.height /
                    ratio).intValue();
            int width = Double.valueOf(srcSize.width /
                    ratio).intValue();
            Size size = new Size(width,
                    height);
            for (MatOfPoint c : contours) {
                MatOfPoint2f c2f =
                        new MatOfPoint2f(c.toArray());
                double peri = Imgproc.arcLength(c2f,
                        true);
                MatOfPoint2f approx =
                        new MatOfPoint2f();
                Imgproc.approxPolyDP(c2f,
                        approx,
                        0.02 *
                                peri,
                        true);
                Point[] points = approx.toArray();
                // select biggest 4 angles polygon
                if (points.length == 4) {
                    Point[] foundPoints = sortPoints(points);
                    if (insideArea(foundPoints,
                            size)) {
                        return new Quadrilateral(c,
                                foundPoints);
                    }
                }
            }
            return null;
        }

        private Point[] sortPoints(Point[] src) {

            ArrayList<Point> srcPoints =
                    new ArrayList<>(Arrays.asList(src));
            Point[] result = {null,
                    null,
                    null,
                    null};
            Comparator<Point> sumComparator =
                    new Comparator<Point>() {
                @Override
                public int compare(Point lhs,
                                   Point rhs) {
                    return Double.valueOf(lhs.y +
                            lhs.x)
                            .compareTo(rhs.y +
                                    rhs.x);
                }
            };

            Comparator<Point> diffComparator =
                    new Comparator<Point>() {

                @Override
                public int compare(Point lhs,
                                   Point rhs) {
                    return Double.valueOf(lhs.y -
                            lhs.x)
                            .compareTo(rhs.y -
                                    rhs.x);
                }
            };

            // top-left corner = minimal sum
            result[0] = Collections.min(srcPoints,
                    sumComparator);

            // bottom-right corner = maximal sum
            result[2] = Collections.max(srcPoints,
                    sumComparator);

            // top-right corner = minimal diference
            result[1] = Collections.min(srcPoints,
                    diffComparator);

            // bottom-left corner = maximal diference
            result[3] = Collections.max(srcPoints,
                    diffComparator);
            return result;
        }

        private boolean insideArea(Point[] rp,
                                   Size size) {

            int width = Double.valueOf(size.width).intValue();
            int height = Double.valueOf(size.height).intValue();
            int baseMeasure = height / 4;
            int bottomPos = height - baseMeasure;
            int leftPos = width / 2 - baseMeasure;
            int rightPos = width / 2 + baseMeasure;
            return (rp[0].x <= leftPos &&
                    rp[0].y <= baseMeasure &&
                    rp[1].x >= rightPos &&
                    rp[1].y <= baseMeasure &&
                    rp[2].x >= rightPos &&
                    rp[2].y >= bottomPos &&
                    rp[3].x <= leftPos &&
                    rp[3].y >= bottomPos);
        }

        private void enhanceDocument(Mat src) {
            if (colorMode &&
                    filterMode) {
                src.convertTo(src,
                        -1,
                        colorGain,
                        colorBias);
                Mat mask =
                        new Mat(src.size(),
                        CvType.CV_8UC1);
                Imgproc.cvtColor(src,
                        mask,
                        Imgproc.COLOR_RGBA2GRAY);
                Mat copy =
                        new Mat(src.size(),
                        CvType.CV_8UC3);
                src.copyTo(copy);
                Imgproc.adaptiveThreshold(mask,
                        mask,
                        255,
                        Imgproc.ADAPTIVE_THRESH_MEAN_C,
                        Imgproc.THRESH_BINARY_INV,
                        15,
                        15);
                src.setTo(new Scalar(255,
                        255,
                        255));
                copy.copyTo(src,
                        mask);
                copy.release();
                mask.release();
                // special color threshold algorithm
                colorThresh(src,
                        colorThresh);
            } else if (!colorMode) {
                Imgproc.cvtColor(src,
                        src,
                        Imgproc.COLOR_RGBA2GRAY);
                if (filterMode) {
                    Imgproc.adaptiveThreshold(src,
                            src,
                            255,
                            Imgproc.ADAPTIVE_THRESH_MEAN_C,
                            Imgproc.THRESH_BINARY,
                            15,
                            15);
                }
            }
        }

        /**
         * When a pixel have any of its
         * three elements above the threshold
         * value add the average of the
         * three values are less than 80% of the
         * higher one, brings all three
         * values to the max possible keeping
         * the relation between them,
         * any absolute white keeps the value, all
         * others go to absolute black.
         * <p>
         * src must be a 3 channel image
         * with 8 bits per channel
         *
         * @param src
         * @param threshold
         */
        private void colorThresh(Mat src,
                                 int threshold) {
            Size srcSize = src.size();
            int size = (int) (srcSize.height *
                    srcSize.width) * 3;
            byte[] d = new byte[size];
            src.get(0,
                    0,
                    d);

            for (int i = 0; i < size; i += 3) {

                // the "& 0xff" operations are needed to convert
                // the signed byte to double
                // avoid unneeded work
                if ((double) (d[i] &
                        0xff) == 255) {
                    continue;
                }

                double max = Math.max(
                        Math.max((double) (d[i] &
                                0xff),
                                (double) (d[i + 1] &
                                        0xff)),
                        (double) (d[i + 2] &
                                0xff));
                double mean = ((double) (d[i] &
                        0xff) +
                        (double) (d[i + 1] &
                                0xff)
                        + (double) (d[i + 2] &
                        0xff)) / 3;

                if (max > threshold &&
                        mean < max * 0.8) {
                    d[i] = (byte) ((double) (d[i] &
                            0xff)
                            * 255 / max);
                    d[i + 1] = (byte) ((double) (d[i + 1] &
                            0xff)
                            * 255 / max);
                    d[i + 2] = (byte) ((double) (d[i + 2] &
                            0xff)
                            * 255 / max);
                } else {
                    d[i] = d[i + 1] = d[i + 2] = 0;
                }
            }
            src.put(0,
                    0,
                    d);
        }

        private Mat fourPointTransform(Mat src,
                                       Point[] pts) {
            double ratio = src.size().height /
                    500;
            Point tl = pts[0];
            Point tr = pts[1];
            Point br = pts[2];
            Point bl = pts[3];
            double widthA = Math.sqrt(
                    Math.pow(br.x -
                            bl.x,
                    2) +
                            Math.pow(br.y -
                                    bl.y,
                    2));
            double widthB = Math.sqrt(
                    Math.pow(tr.x -
                            tl.x,
                    2) + Math.pow(
                            tr.y -
                                    tl.y,
                    2));
            double dw = Math.max
                    (widthA,
                    widthB) *
                    ratio;
            int maxWidth = Double.valueOf(
                    dw).intValue();
            double heightA = Math.sqrt(
                    Math.pow(tr.x -
                            br.x,
                    2) +
                            Math.pow(tr.y -
                                    br.y,
                    2));
            double heightB = Math.sqrt(
                    Math.pow(tl.x -
                            bl.x,
                    2) +
                            Math.pow(tl.y -
                                    bl.y,
                    2));
            double dh = Math.max(heightA,
                    heightB) *
                    ratio;
            int maxHeight = Double.valueOf(
                    dh).intValue();
            Mat doc = new Mat(maxHeight,
                    maxWidth,
                    CvType.CV_8UC4);
            Mat src_mat = new Mat(4,
                    1,
                    CvType.CV_32FC2);
            Mat dst_mat = new Mat(4,
                    1,
                    CvType.CV_32FC2);
            src_mat.put(0,
                    0,
                    tl.x *
                            ratio,
                    tl.y *
                            ratio,
                    tr.x *
                            ratio,
                    tr.y *
                            ratio,
                    br.x *
                            ratio,
                    br.y *
                            ratio,
                    bl.x *
                            ratio,
                    bl.y *
                            ratio);
            dst_mat.put(0,
                    0,
                    0.0,
                    0.0,
                    dw,
                    0.0,
                    dw,
                    dh,
                    0.0,
                    dh);
            Mat m = Imgproc.getPerspectiveTransform(
                    src_mat,
                    dst_mat);
            Imgproc.warpPerspective(src,
                    doc,
                    m,
                    doc.size());
            return doc;
        }

        private ArrayList<MatOfPoint> findContours(Mat src) {
            Mat grayImage;
            Mat cannedImage;
            Mat resizedImage;
            double ratio = src.size().height /
                    500;
            int height = Double.valueOf(
                    src.size().height /
                            ratio)
                    .intValue();
            int width = Double.valueOf(
                    src.size().width /
                            ratio)
                    .intValue();
            Size size = new Size(width,
                    height);
            resizedImage = new Mat(size,
                    CvType.CV_8UC4);
            grayImage = new Mat(size,
                    CvType.CV_8UC4);
            cannedImage = new Mat(size,
                    CvType.CV_8UC1);
            Imgproc.resize(src,
                    resizedImage,
                    size);
            Imgproc.cvtColor(resizedImage,
                    grayImage,
                    Imgproc.COLOR_RGBA2GRAY,
                    4);
            Imgproc.GaussianBlur(grayImage,
                    grayImage,
                    new Size(5,
                            5),
                    0);
            Imgproc.Canny(grayImage,
                    cannedImage,
                    75,
                    200);
            ArrayList<MatOfPoint> contours =
                    new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(cannedImage,
                    contours,
                    hierarchy,
                    Imgproc.RETR_LIST,
                    Imgproc.CHAIN_APPROX_SIMPLE);
            hierarchy.release();
            Collections.sort(contours,
                    new Comparator<MatOfPoint>() {
                        @Override
                        public int compare(MatOfPoint lhs,
                                           MatOfPoint rhs) {
                            return Double.valueOf(Imgproc.contourArea(rhs))
                                    .compareTo(Imgproc.contourArea(lhs));
                        }
                    });

            resizedImage.release();
            grayImage.release();
            cannedImage.release();
            return contours;
        }

        public Result[] zxing(Mat inputImage) throws ChecksumException,
                FormatException {
            int w = inputImage.width();
            int h = inputImage.height();
            Mat southEast;

            southEast = inputImage.submat(0,
                    h / 4,
                    w /
                            2 +
                            h /
                                    4,
                    w);
            Bitmap bMap = Bitmap.createBitmap(southEast.width(),
                    southEast.height(),
                    Bitmap.Config.ARGB_8888);
            org.opencv.android.Utils.matToBitmap(southEast,
                    bMap);
            southEast.release();
            int[] intArray =
                    new int[bMap.getWidth() *
                    bMap.getHeight()];
            //copy pixel data from the Bitmap
            // into the 'intArray' array
            bMap.getPixels(intArray,
                    0,
                    bMap.getWidth(),
                    0,
                    0,
                    bMap.getWidth(),
                    bMap.getHeight());
            LuminanceSource source =
                    new RGBLuminanceSource(bMap.getWidth(),
                            bMap.getHeight(),
                            intArray);
            BinaryBitmap bitmap =
                    new BinaryBitmap(
                            new HybridBinarizer(source));
            Result[] results = {};
            try {
                results = qrCodeMultiReader.decodeMultiple(
                        bitmap);
            } catch (NotFoundException e) {
            }
            return results;

        }
    }
}
