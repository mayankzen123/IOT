package com.example.mayank.iot.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.Preference;
import android.util.Log;

import com.example.mayank.iot.MainActivity;
import com.example.mayank.iot.Model.DHT11Model;
import com.example.mayank.iot.Model.TorsionModel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mayank on 3/17/2017.
 */
public class Util {
    public static int MAX_DEVIATION = 5;
    final static String TAG = MainActivity.class.getSimpleName();
    public static final String MY_PREFS_NAME = "IotPrefFile";

    public static boolean saveTorsionExcelFile(Context context, String fileName, List<TorsionModel.FeedsEntity> feedsEntityList, String[] headerName, String workSheetName) {
        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);

        //Cell style for Data 1 row
        CellStyle dataWhiteStyle = wb.createCellStyle();
        dataWhiteStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        dataWhiteStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dataWhiteStyle.setAlignment(CellStyle.ALIGN_CENTER);

        //Cell style for Data 1 row
        CellStyle dataBlueStyle = wb.createCellStyle();
        dataBlueStyle.setFillForegroundColor(HSSFColor.BLUE.index);
        dataBlueStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dataBlueStyle.setAlignment(CellStyle.ALIGN_CENTER);
        Font font = wb.createFont();
        font.setColor(HSSFColor.WHITE.index);
        dataBlueStyle.setFont(font);

        //Cell style for FAIL RED Result
        CellStyle dataGreenStyle = wb.createCellStyle();
        dataGreenStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        dataGreenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dataGreenStyle.setAlignment(CellStyle.ALIGN_CENTER);
        dataGreenStyle.setFont(font);

        //Cell style for PASS GREEN Result
        CellStyle dataRedStyle = wb.createCellStyle();
        dataRedStyle.setFillForegroundColor(HSSFColor.RED.index);
        dataRedStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dataRedStyle.setAlignment(CellStyle.ALIGN_CENTER);
        dataRedStyle.setFont(font);


        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet(workSheetName);

        // Generate column headings
        Row headerRow = sheet1.createRow(0);
        for (int j = 0; j < 9; j++) {
            c = headerRow.createCell(j);
            c.setCellValue(headerName[j]);
            c.setCellStyle(headerStyle);
            sheet1.setColumnWidth(j, (15 * 300));
        }

        //Put Data Under Column headings
        for (int i = 0; i < feedsEntityList.size(); i++) {
            Row dataRow = sheet1.createRow(i + 1);
            for (int j = 0; j < 9; j++) {
                boolean ignore = false;
                c = dataRow.createCell(j);
                if (j == 0)
                    c.setCellValue(feedsEntityList.get(i).getEntry_id());
                if (j == 1)
                    c.setCellValue(feedsEntityList.get(i).getCreated_at().split("T")[0]);
                if (j == 2)
                    c.setCellValue(feedsEntityList.get(i).getField2());
                if (j == 3)
                    c.setCellValue(feedsEntityList.get(i).getField3());
                if (j == 4)
                    c.setCellValue(feedsEntityList.get(i).getField4());
                if (j == 5)
                    c.setCellValue(feedsEntityList.get(i).getField5());
                if (j == 6)
                    c.setCellValue(feedsEntityList.get(i).getField6());
                if (j == 7)
                    c.setCellValue(feedsEntityList.get(i).getField7());
                if (j == 8) {
                    float maxValue, minValue;
                    if (!feedsEntityList.get(i).getField7().equalsIgnoreCase("") && !feedsEntityList.get(i).getField6().equalsIgnoreCase("")) {
                        if (Float.parseFloat(feedsEntityList.get(i).getField7()) > Float.parseFloat(feedsEntityList.get(i).getField6())) {
                            maxValue = Float.parseFloat(feedsEntityList.get(i).getField7());
                            minValue = Float.parseFloat(feedsEntityList.get(i).getField6());
                        } else {
                            maxValue = Float.parseFloat(feedsEntityList.get(i).getField6());
                            minValue = Float.parseFloat(feedsEntityList.get(i).getField7());
                        }
                        float deviation = maxValue - minValue;
                        if (deviation > MAX_DEVIATION) {
                            c.setCellStyle(dataRedStyle);
                            c.setCellValue("FAIL");
                            ignore = true;

                        } else {
                            c.setCellValue("PASS");
                            c.setCellStyle(dataGreenStyle);
                            ignore = true;
                        }
                    }
                }
                sheet1.setColumnWidth(j, (15 * 300));
                if (i % 2 == 0) {
                    if (!ignore)
                        c.setCellStyle(dataBlueStyle);
                } else {
                    if (!ignore)
                        c.setCellStyle(dataWhiteStyle);
                }
            }

        }

        // Create a path where we will place our List of objects on external storage
        FileOutputStream os = null;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/IOT");
        myDir.mkdirs();

        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static boolean saveDht11ExcelFile(Context context, String fileName, List<DHT11Model.FeedsEntity> feedsEntityList, String[] headerName, String workSheetName) {
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);

        //Cell style for Data 1 row
        CellStyle dataWhiteStyle = wb.createCellStyle();
        dataWhiteStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        dataWhiteStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dataWhiteStyle.setAlignment(CellStyle.ALIGN_CENTER);

        //Cell style for Data 1 row
        CellStyle dataBlueStyle = wb.createCellStyle();
        dataBlueStyle.setFillForegroundColor(HSSFColor.BLUE.index);
        dataBlueStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        dataBlueStyle.setAlignment(CellStyle.ALIGN_CENTER);
        Font font = wb.createFont();
        font.setColor(HSSFColor.WHITE.index);
        dataBlueStyle.setFont(font);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet(workSheetName);

        // Generate column headings
        Row headerRow = sheet1.createRow(0);
        for (int j = 0; j < 4; j++) {
            c = headerRow.createCell(j);
            c.setCellValue(headerName[j]);
            c.setCellStyle(headerStyle);
            sheet1.setColumnWidth(j, (15 * 300));
        }

        //Put Data Under Column headings
        for (int i = 0; i < feedsEntityList.size(); i++) {
            Row dataRow = sheet1.createRow(i + 1);
            for (int j = 0; j < 4; j++) {
                boolean ignore = false;
                c = dataRow.createCell(j);
                if (j == 0)
                    c.setCellValue(feedsEntityList.get(i).getEntry_id());
                if (j == 1)
                    c.setCellValue(feedsEntityList.get(i).getCreated_at().split("T")[0]);
                if (j == 2)
                    c.setCellValue(feedsEntityList.get(i).getField1());
                if (j == 3)
                    c.setCellValue(feedsEntityList.get(i).getField2());
                sheet1.setColumnWidth(j, (15 * 300));
                if (i % 2 == 0) {
                    if (!ignore)
                        c.setCellStyle(dataBlueStyle);
                } else {
                    if (!ignore)
                        c.setCellStyle(dataWhiteStyle);
                }
            }

        }

        // Create a path where we will place our List of objects on external storage
        FileOutputStream os = null;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/IOT");
        myDir.mkdirs();

        String fname = fileName;
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    public static void saveDataToSharedPref(Context context, String ip, String port) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("serverIp", ip);
        editor.putString("serverPort", port);
        editor.commit();
    }

    public static SharedPreferences getSharePref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs;
    }

}
