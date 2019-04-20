package com.luminous.mpartner.activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.luminous.mpartner.R;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.ConnectDownloadPDF;
import com.luminous.mpartner.network.entities.ConnectPoint;
import com.luminous.mpartner.network.entities.ConnectReport;
import com.luminous.mpartner.network.entities.Scheme;
import com.luminous.mpartner.utilities.AppConstants;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class WRSStatusFragmentMain extends Fragment {

    //Comment out code for old reports by Anusha 31/05/2018 TASK#3783
    TextView tvmonth, tvdate;
    EditText srno;
    Button display;
    String date;
    Date currentDate, currentMonth;
    private Calendar mCurrentDate, mCurrentMonth, mJoinDate, mMonth;
    private android.app.DatePickerDialog joinDatePickerDialog, DatePickerDialog;
    private SimpleDateFormat dateFormatter, monthFormatter;
    private final int REQUEST_CAMERA_USE = 200;
    private String TAG;
    LinearLayout llserial, llmonth, lldate, llListDate, llEntered;
    RadioGroup radioGroupSelect, radioGroupEntered;
    Boolean rbselected = false, rbentered = false;
    ListView listView, listDate ;
    int selected = 0;
/*    private ArrayList<MonthReport> mMonthReport;
    private ArrayList<DateReport> mDateReport;
    private ArrayList<SerialReport> mSerialReport;
    private MonthReportsAdapter mAdapter;
    private SerialReportsAdapter mAdapterSerial;
    private DateReportsAdapter mAdapterDate;*/
    ImageView scanner;

    //Check for version
    String currentVersion, latestVersion;
    Dialog dialog;

    private Spinner spSchemes;
    private ArrayList<Scheme> mSchemes;
    public String mSelectedScheme = "", entered = "";
    Toast t;
    private View vRootView;
    private static WRSStatusFragmentMain instance;
    Boolean visible = false;

    //Add new report by Anusha 31/05/2018 TASK#3783
    TextView tvSubmitted, tvAccepted, tvRejected, tvEarned, tvCurrent, tvNext;
    LinearLayout llSubmitted, llAccepted, llRejected, llEarned, llCurrent, llNext, llinear;
    private ArrayList<ConnectReport> mReport;

    private ArrayList<ConnectPoint> mPoint;

    //Download PDF by Anusha 28/06/2018 TASK#4009
    TextView tvAttach;
    private ArrayList<ConnectDownloadPDF> mDownload;
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    private SharedPrefsManager sharedPrefsManager;



    /*TODO : YASH*/
    private RetrofitInterface apiInterface;

    public static WRSStatusFragmentMain getInstance() {
        // instance = noti InverlastFragment();
        return instance;
    }

    public WRSStatusFragmentMain() {
        // Required empty public constructor
    }

    public static WRSStatusFragmentMain newInstance(String title) {
        WRSStatusFragmentMain fragment = new WRSStatusFragmentMain();

        Bundle args = new Bundle();
        args.putCharSequence("title", title);
        fragment.setArguments(args);

        return fragment;
    }


    /*TODO : YASH*/
    private void WRSrNoExistenceUpdate(String serialNumber){

        if (!CommonUtility.isNetworkAvailable(getContext())) {
            return;
        }

        String url = ServerConfig.getWRSrNoExistenceUpdateUrl(serialNumber);
        apiInterface.getWRSrNoExistanceUpdate(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {


                        Log.d(TAG, "onNext");

                        try {
                            String response = responseBody.string();

                            t.makeText(getContext(), "Response : "+response, Toast.LENGTH_SHORT).show();

                            /*TODO : do something with response*/


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d(TAG, "onError : "+e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

    }

    @Override
    public void setUserVisibleHint(boolean menuVisible) {
        if (menuVisible)
            visible = true;
        else
            visible = false;

        super.setUserVisibleHint(menuVisible);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        if (visible) {
            vRootView = inflater.inflate(R.layout.activity_wrsstatus, container, false);
            instance = this;

            sharedPrefsManager = new SharedPrefsManager(getContext());
            //Create New Alert by Anusha 12/07/2018 TASK#4009
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Disclaimer");
            builder.setMessage("Points in Connect+ report are subject to change. Points will be deducted if Customer details are found inaccurate in the verification.");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setCancelable(false);
            builder.show();

            t = new Toast(getActivity().getApplicationContext());
            //Requesting storage permission
            requestStoragePermission();
            prepareViews();

            return vRootView;
        } else
            return null;
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrsstatus);
        t = noti Toast(getApplicationContext());
        //Check current version
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        //Check Play Store Version
        VersionChecker versionChecker = noti VersionChecker();
        latestVersion = null;
        try {
            latestVersion = versionChecker.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Compare Current Version with Playstore Version
        if (latestVersion != null) {
            //if (!currentVersion.equalsIgnoreCase(latestVersion)) {
            if (Float.parseFloat(currentVersion) < Float.parseFloat(latestVersion) && !currentVersion.equalsIgnoreCase(latestVersion)) {
                if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                    showUpdateDialog();
                }
            }
        }
        prepareViews();
    }

    private void showUpdateDialog(){
        final AlertDialog.Builder builder = noti AlertDialog.Builder(this);
        builder.setTitle("Update");
        builder.setMessage("A New Update is Available");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Update", noti DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(noti Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=com.luminous.mpartner")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", noti DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExitActivity.exitApplication(getApplicationContext());
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }*/

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void prepareViews() {
        spSchemes = (Spinner) vRootView.findViewById(R.id.fragment_reports_sp_schemes);


        //Add new report by Anusha 31/05/2018 TASK#3783
        tvSubmitted = (TextView) vRootView.findViewById(R.id.tvSubmitted);
        tvAccepted = (TextView) vRootView.findViewById(R.id.tvAccepted);
        tvRejected = (TextView) vRootView.findViewById(R.id.tvRejected);
        tvEarned = (TextView) vRootView.findViewById(R.id.tvEarned);
        tvCurrent = (TextView) vRootView.findViewById(R.id.tvCurrent);
        tvNext = (TextView) vRootView.findViewById(R.id.tvNext);
        llSubmitted = (LinearLayout) vRootView.findViewById(R.id.llSubmitted);
        llAccepted = (LinearLayout) vRootView.findViewById(R.id.llAccepted);
        llRejected = (LinearLayout) vRootView.findViewById(R.id.llRejected);
        llEarned = (LinearLayout) vRootView.findViewById(R.id.llEarned);
        llCurrent = (LinearLayout) vRootView.findViewById(R.id.llCurrent);
        //llNext = (LinearLayout) vRootView.findViewById(R.id.llNext);
        llinear = (LinearLayout) vRootView.findViewById(R.id.llinear); //Add new report by Anusha 4/06/2018 TASK#3783
        tvAttach = (TextView) vRootView.findViewById(R.id.tvAttach);
        getSchemes();

        //Download PDF by Anusha 28/06/2018 TASK#4009
        tvAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
                    getDownloadPDF(mSelectedScheme, "dist");
                } else {
                    getDownloadPDF(mSelectedScheme, "dlr");
                }
            }
        });

    }

    //Download PDF by Anusha 28/06/2018 TASK#4009
    public void getDownloadPDF(String scheme, String type) {
//        UserController userController = new UserController(getActivity(), "getPDF");
//        userController.downloadPDF(scheme, type);
    }

    public void getPDF(Object object) {
//        mDownload = ((UserController) object).mDownload;
//        if (mDownload != null) {
//
//            try {
//                descriptionTable(mDownload);
//            }catch (BadElementException e){
//                e.printStackTrace();
//            }
//        }
    }

    // Method for creating a pdf file from text, saving it then opening it for display
    public void createandDisplayPdf(String text) {

        Document doc = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "newFile.pdf");
            FileOutputStream fOut = new FileOutputStream(file);

            PdfWriter.getInstance(doc, fOut);

            //open the document
            doc.open();

            Paragraph p1 = new Paragraph(text);
            //Font paraFont= new Font(Font.COURIER);
            p1.setAlignment(Paragraph.ALIGN_CENTER);
            //p1.setFont(paraFont);

            //add paragraph to document
            doc.add(p1);

        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } finally {
            doc.close();
        }

        viewPdf("newFile.pdf", "Dir");
    }

    public void descriptionTable(ArrayList<ConnectDownloadPDF> list1) throws BadElementException {
        Document p = new Document();
        FileOutputStream fOut = null;
        try {
            String path = Environment.getExternalStorageDirectory() + "/Dir";

            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            //Create Excel by Anusha 24/07/2018 TASK#3967
            File file = new File(dir, "newFile.xls");

            //New Workbook
            Workbook wb = new HSSFWorkbook();

            Cell c = null;

            //Cell style for header row
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.YELLOW.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            CellStyle cs1 = wb.createCellStyle();
            cs1.setFillForegroundColor(HSSFColor.WHITE.index);
            cs1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            //New Sheet
            Sheet sheet1 = null;
            sheet1 = wb.createSheet("myOrder");

            // Generate column headings
            Row row = sheet1.createRow(0);

            c = row.createCell(0);
            c.setCellValue("S. No");
            c.setCellStyle(cs);

            c = row.createCell(1);
            c.setCellValue("Product_Type");
            c.setCellStyle(cs);

            c = row.createCell(2);
            c.setCellValue("Dis_Sap_Code");
            c.setCellStyle(cs);

            c = row.createCell(3);
            c.setCellValue("Dis_Name");
            c.setCellStyle(cs);

            c = row.createCell(4);
            c.setCellValue("Dlr_Sap_Code");
            c.setCellStyle(cs);

            c = row.createCell(5);
            c.setCellValue("Dlr_Name");
            c.setCellStyle(cs);

            c = row.createCell(6);
            c.setCellValue("product_code");
            c.setCellStyle(cs);

            c = row.createCell(7);
            c.setCellValue("product_detail");
            c.setCellStyle(cs);

            c = row.createCell(8);
            c.setCellValue("Sale_Date");
            c.setCellStyle(cs);

            c = row.createCell(9);
            c.setCellValue("cust_Name");
            c.setCellStyle(cs);

            c = row.createCell(10);
            c.setCellValue("cust_number");
            c.setCellStyle(cs);

            c = row.createCell(11);
            c.setCellValue("cust_add");
            c.setCellStyle(cs);

            c = row.createCell(12);
            c.setCellValue("dis_state");
            c.setCellStyle(cs);

            c = row.createCell(13);
            c.setCellValue("dlr_state");
            c.setCellStyle(cs);

            c = row.createCell(14);
            c.setCellValue("Point");
            c.setCellStyle(cs);

            c = row.createCell(15);
            c.setCellValue("Status");
            c.setCellStyle(cs);

            c = row.createCell(16);
            c.setCellValue("Remark");
            c.setCellStyle(cs);

            c = row.createCell(17);
            c.setCellValue("Created_On");
            c.setCellStyle(cs);

            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));
            sheet1.setColumnWidth(6, (15 * 500));
            sheet1.setColumnWidth(7, (15 * 500));
            sheet1.setColumnWidth(8, (15 * 500));
            sheet1.setColumnWidth(9, (15 * 500));
            sheet1.setColumnWidth(10, (15 * 500));
            sheet1.setColumnWidth(11, (15 * 500));
            sheet1.setColumnWidth(12, (15 * 500));
            sheet1.setColumnWidth(13, (15 * 500));
            sheet1.setColumnWidth(14, (15 * 500));
            sheet1.setColumnWidth(15, (15 * 500));
            sheet1.setColumnWidth(16, (15 * 500));
            sheet1.setColumnWidth(17, (15 * 500));

            //Cell c1 = null;

            int i = 1;
            for (ConnectDownloadPDF report : list1) {
                /*int rows=sheet1.getLastRowNum();
                sheet1.shiftRows(2,rows,1);*/

                Row row1 = sheet1.createRow(i);
                Cell c1 = null;

                c1 = row1.createCell(0);
                c1.setCellValue(report.SerialNo);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(1);
                c1.setCellValue(report.ProductType);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(2);
                c1.setCellValue(report.Dis_Sap_Code);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(3);
                c1.setCellValue(report.Dis_Name);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(4);
                c1.setCellValue(report.Dlr_Sap_Code);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(5);
                c1.setCellValue(report.Dlr_Name);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(6);
                c1.setCellValue(report.product_code);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(7);
                c1.setCellValue(report.product_detail);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(8);
                c1.setCellValue(report.Sale_Date);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(9);
                c1.setCellValue(report.cust_Name);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(10);
                c1.setCellValue(report.cust_number);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(11);
                c1.setCellValue(report.cust_add);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(12);
                c1.setCellValue(report.dis_state);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(13);
                c1.setCellValue(report.dlr_state);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(14);
                c1.setCellValue(report.Point);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(15);
                c1.setCellValue(report.Status);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(16);
                c1.setCellValue(report.Remark);
                //c1.setCellStyle(cs1);

                c1 = row1.createCell(17);
                c1.setCellValue(report.Created_On);
                //c1.setCellStyle(cs1);

                sheet1.setColumnWidth(0, (15 * 500));
                sheet1.setColumnWidth(1, (15 * 500));
                sheet1.setColumnWidth(2, (15 * 500));
                sheet1.setColumnWidth(3, (15 * 500));
                sheet1.setColumnWidth(4, (15 * 500));
                sheet1.setColumnWidth(5, (15 * 500));
                sheet1.setColumnWidth(6, (15 * 500));
                sheet1.setColumnWidth(7, (15 * 500));
                sheet1.setColumnWidth(8, (15 * 500));
                sheet1.setColumnWidth(9, (15 * 500));
                sheet1.setColumnWidth(10, (15 * 500));
                sheet1.setColumnWidth(11, (15 * 500));
                sheet1.setColumnWidth(12, (15 * 500));
                sheet1.setColumnWidth(13, (15 * 500));
                sheet1.setColumnWidth(14, (15 * 500));
                sheet1.setColumnWidth(15, (15 * 500));
                sheet1.setColumnWidth(16, (15 * 500));
                sheet1.setColumnWidth(17, (15 * 500));

                i = i + 1;

            }

            fOut = new FileOutputStream(file);
            wb.write(fOut);
        /*File file = new File(dir, "newFile.pdf");
        FileOutputStream fOut = new FileOutputStream(file);

        PdfWriter.getInstance(p, fOut);
        //open the document
        p.open();

        PdfPTable table = new PdfPTable(18);

        table.setWidthPercentage(100);

        //Add headers to PDF by Anusha 19/07/2018 TASK#4009
            Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);

            PdfPCell c = new PdfPCell(new Phrase("S. No", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            //   c1.setBorderColor(celalBorderColor);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Product_Type", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Dis_Sap_Code", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Dis_Name", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Dlr_Sap_Code", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Dlr_Name", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("product_code", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("product_detail", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Sale_Date", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("cust_Name", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("cust_number", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("cust_add", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("dis_state", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("dlr_state", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Point", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Status", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Remark", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            c = new PdfPCell(new Phrase("Created_On", boldFont));
            c.setHorizontalAlignment(Element.ALIGN_LEFT);
            c.setBorderWidth(1);
            c.setColspan(1);
            c.setPaddingBottom(10);
            c.setBackgroundColor(BaseColor.YELLOW);
            table.addCell(c);

            for (ConnectDownloadPDF report : list1) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(report.SerialNo);
                PdfPCell c1 = new PdfPCell(new Phrase(report.SerialNo));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                //   c1.setBorderColor(celalBorderColor);
                table.addCell(c1);

                list.add(report.ProductType);
                c1 = new PdfPCell(new Phrase(report.ProductType));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Dis_Sap_Code);
                c1 = new PdfPCell(new Phrase(report.Dis_Sap_Code));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Dis_Name);
                c1 = new PdfPCell(new Phrase(report.Dis_Name));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Dlr_Sap_Code);
                c1 = new PdfPCell(new Phrase(report.Dlr_Sap_Code));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Dlr_Name);
                c1 = new PdfPCell(new Phrase(report.Dlr_Name));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.product_code);
                c1 = new PdfPCell(new Phrase(report.product_code));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.product_detail);
                c1 = new PdfPCell(new Phrase(report.product_detail));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Sale_Date);
                c1 = new PdfPCell(new Phrase(report.Sale_Date));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.cust_Name);
                c1 = new PdfPCell(new Phrase(report.cust_Name));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.cust_number);
                c1 = new PdfPCell(new Phrase(report.cust_number));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.cust_add);
                c1 = new PdfPCell(new Phrase(report.cust_add));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.dis_state);
                c1 = new PdfPCell(new Phrase(report.dis_state));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.dlr_state);
                c1 = new PdfPCell(new Phrase(report.dlr_state));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Point);
                c1 = new PdfPCell(new Phrase(report.Point));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Status);
                c1 = new PdfPCell(new Phrase(report.Status));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Remark);
                c1 = new PdfPCell(new Phrase(report.Remark));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);

                list.add(report.Created_On);
                c1 = new PdfPCell(new Phrase(report.Created_On));
                c1.setHorizontalAlignment(Element.ALIGN_LEFT);
                c1.setBorderWidth(1);
                c1.setColspan(1);
                c1.setPaddingBottom(10);
                table.addCell(c1);
            }

            try {
                p.add(table);
            } catch (DocumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (DocumentException de) {
            Log.e("PDFCreator", "DocumentException:" + de);*/
        } catch (IOException e) {
            Log.e("PDFCreator", "ioException:" + e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            //p.close();
            try {
                if (null != fOut)
                    fOut.close();
            } catch (Exception ex) {
            }
        }
        //viewPdf("newFile.pdf", "Dir");
        viewPdf("newFile.xls", "Dir");
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory) {

        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        //pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setDataAndType(path, "application/vnd.ms-excel"); //Create Excel by Anusha 24/07/2018 TASK#3967
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            //Make Length Long for Toast by Anusha 01/08/2018 TASK#4307
            Toast.makeText(getActivity(), "Can't read excel file", Toast.LENGTH_LONG).show();
        }
    }

   /* private DatePickerDialog createDialogWithoutDateField(android.app.DatePickerDialog.OnDateSetListener dsl, int year, int month, int date) {
        DatePickerDialog dpd = noti DatePickerDialog(this, dsl, year, month, date);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return dpd;
    }*/

    /*public void selectMonth(View v)
    {
        tvmonth.setOnClickListener(noti View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.show();
            }
        });

    }*/

    /*public void selectDate(View v)
    {
        tvdate.setOnClickListener(noti View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinDatePickerDialog.show();
            }
        });
    }*/

    /*public void openScanner(View v) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    noti String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_USE);

            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            if (isCameraAvailable()) {
                Intent intent = noti Intent(getActivity(), SimpleScannerActivity.class);
                startActivityForResult(intent, AppConstants.ZBAR_SCANNER_REQUEST);
            } else {
                Toast.makeText(getActivity(), "Rear Facing Camera Unavailable",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_USE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission Granted");
                } else {
                    Log.e(TAG, "Permission Denied");
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getActivity().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstants.ZBAR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    updateSerialNumber(data.getStringExtra(AppConstants.SCAN_RESULT));
                } else if (resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(AppConstants.ERROR_INFO);
                    if (!TextUtils.isEmpty(error)) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void updateSerialNumber(String serialNumber) {
        srno.setText("" + serialNumber);
        getSerialNumberValidation();
    }

    private void getSerialNumberValidation() {
        UserController userController = new UserController(getActivity(), "updateSerialNumberValidation1");
        userController.getSerialNumberValidation(srno.getText().toString());
    }

    public void updateSerialNumberValidation1(Object object) {
        boolean isSerialNumberValid = ((UserController) object).isSerialValid;
      //  llForm.setVisibility(isSerialNumberValid ? View.VISIBLE : View.GONE);
        if (!isSerialNumberValid) {
            Global.showToast("" + ((UserController) object).mMessage);
        }
    }*/

    public void getSchemes() {
//        UserController userController = new UserController(getActivity(), "updateSchemes1");
//        userController.getSchemes();
    }

   /* public void updateSchemes1(Object object) {
//        mSchemes = ((UserController) object).mSchemeList;
        if (mSchemes != null) {
            spSchemes.setAdapter(new SchemesDropdownAdapter(getActivity(), mSchemes));
            spSchemes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mSelectedScheme = position > 0 ? mSchemes.get(position - 1).schemeName : "";
                    //Add new report by Anusha 1/06/2018 TASK#3783
                    llSubmitted.setVisibility(View.GONE);
                    llAccepted.setVisibility(View.GONE);
                    llRejected.setVisibility(View.GONE);
                    if (position > 0) {
                        //New report by Anusha 4/06/2018 TASK#3783
                        llinear.setVisibility(View.VISIBLE);
                        tvSubmitted.setText("");
                        tvAccepted.setText("");
                        tvRejected.setText("");
                        tvEarned.setText("");
                        tvCurrent.setText("");
                        tvNext.setText("");
                        if (sharedPrefsManager.getString(SharedPreferenceKeys.USER_TYPE).equalsIgnoreCase(AppConstants.USER_DISTRIBUTOR)) {
                            getConnectReport(mSelectedScheme, "dist");
                            getConnectReportPoint(mSelectedScheme, "dist"); //New report by Anusha 4/06/2018 TASK#3783
                        } else {
                            getConnectReport(mSelectedScheme, "dlr");
                            getConnectReportPoint(mSelectedScheme, "dlr"); //New report by Anusha 4/06/2018 TASK#3783
                        }
                    } else { //New report by Anusha 4/06/2018 TASK#3783
                        llinear.setVisibility(View.GONE);
                        tvSubmitted.setText("");
                        tvAccepted.setText("");
                        tvRejected.setText("");
                        tvEarned.setText("");
                        tvCurrent.setText("");
                        tvNext.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }*/

    public void getConnectReport(String scheme, String type) {
//        UserController userController = new UserController(getActivity(), "getReport");
//        userController.getConnectReport(scheme, type);
    }

    public void getReport(Object object) {
//        mReport = ((UserController) object).mReport;
        if (mReport != null) {
            //Add new report by Anusha 1/06/2018 TASK#3783

            for (ConnectReport report : mReport) {
                if (report.Particulars.contains("Submitted")) {
                    llSubmitted.setVisibility(View.VISIBLE);
                    TextView tvhups = (TextView) vRootView.findViewById(R.id.tvHups);
                    TextView tvbattery = (TextView) vRootView.findViewById(R.id.tvBattery);
                    TextView tvhkva = (TextView) vRootView.findViewById(R.id.tvSolar);
                    TextView tvpanel = (TextView) vRootView.findViewById(R.id.tvPanel);
                    TextView tvstabilizer = (TextView) vRootView.findViewById(R.id.tvStabilizer);

                    tvhups.setText(report.Hups);
                    tvbattery.setText(report.Battery);
                    tvhkva.setText(report.Hkva);
                    tvpanel.setText(report.Panel);
                    tvstabilizer.setText(report.Stabilizer);
                    //New report by Anusha 4/06/2018 TASK#3783
                    tvSubmitted.setText(String.valueOf(Integer.parseInt(report.Hups) + Integer.parseInt(report.Battery) + Integer.parseInt(report.Hkva) + Integer.parseInt(report.Panel) + Integer.parseInt(report.Stabilizer)));
                } else if (report.Particulars.contains("Accepted")) {
                    llAccepted.setVisibility(View.VISIBLE);
                    TextView tvhups = (TextView) vRootView.findViewById(R.id.tvHups1);
                    TextView tvbattery = (TextView) vRootView.findViewById(R.id.tvBattery1);
                    TextView tvhkva = (TextView) vRootView.findViewById(R.id.tvSolar1);
                    TextView tvpanel = (TextView) vRootView.findViewById(R.id.tvPanel1);
                    TextView tvstabilizer = (TextView) vRootView.findViewById(R.id.tvStabilizer1);

                    tvhups.setText(report.Hups);
                    tvbattery.setText(report.Battery);
                    tvhkva.setText(report.Hkva);
                    tvpanel.setText(report.Panel);
                    tvstabilizer.setText(report.Stabilizer);
                    //New report by Anusha 4/06/2018 TASK#3783
                    tvAccepted.setText(String.valueOf(Integer.parseInt(report.Hups) + Integer.parseInt(report.Battery) + Integer.parseInt(report.Hkva) + Integer.parseInt(report.Panel) + Integer.parseInt(report.Stabilizer)));
                } else if (report.Particulars.contains("Rejected")) {
                    llRejected.setVisibility(View.VISIBLE);
                    TextView tvhups = (TextView) vRootView.findViewById(R.id.tvHups2);
                    TextView tvbattery = (TextView) vRootView.findViewById(R.id.tvBattery2);
                    TextView tvhkva = (TextView) vRootView.findViewById(R.id.tvSolar2);
                    TextView tvpanel = (TextView) vRootView.findViewById(R.id.tvPanel2);
                    TextView tvstabilizer = (TextView) vRootView.findViewById(R.id.tvStabilizer2);

                    tvhups.setText(report.Hups);
                    tvbattery.setText(report.Battery);
                    tvhkva.setText(report.Hkva);
                    tvpanel.setText(report.Panel);
                    tvstabilizer.setText(report.Stabilizer);
                    //New report by Anusha 4/06/2018 TASK#3783
                    tvRejected.setText(String.valueOf(Integer.parseInt(report.Hups) + Integer.parseInt(report.Battery) + Integer.parseInt(report.Hkva) + Integer.parseInt(report.Panel) + Integer.parseInt(report.Stabilizer)));
                }
            }
        }
    }

    //Add new report by Anusha 4/06/2018 TASK#3783
    public void getConnectReportPoint(String scheme, String type) {
//        UserController userController = new UserController(getActivity(), "getPoint");
//        userController.getConnectReportPoint(scheme, type);
    }

    public void getPoint(Object object) {
//        mPoint = ((UserController) object).mPoint;
//        if (mPoint != null) {
//            for (ConnectPoint report : mPoint) {
//                tvEarned.setText(report.Earned);
//                tvCurrent.setText(report.Current);
//                tvNext.setText(report.Next);
//            }
//        }
    }

    /*public void displayList(View v){
        if (mSelectedScheme.equalsIgnoreCase("")) {
            t.cancel();
            t = Toast.makeText(getActivity(), "Please select a Scheme", Toast.LENGTH_SHORT);
            t.show();
        }
        else if (rbselected == false){
            t.cancel();
            t = Toast.makeText(getActivity(), "Please select an option", Toast.LENGTH_SHORT);
            t.show();
        }
        else if (selected == 2 && srno.getText().length() == 0){
            t.cancel();
            t = Toast.makeText(getActivity(), "Please enter a Serial Number", Toast.LENGTH_SHORT);
            t.show();
        }
        else if (selected == 4 && rbentered == false){
            t.cancel();
            t = Toast.makeText(getActivity(), "Please select Entered By", Toast.LENGTH_SHORT);
            t.show();
        }
        else {
            if (selected == 2)
                getSerialList(srno.getText().toString(), mSelectedScheme, "1");
            else if (selected == 1)
                getMonthList(mSelectedScheme);
            else if (selected == 3)
                getDateList(mSelectedScheme, tvdate.getText().toString(), "0", "oth");
            else if (selected == 4) {
                Intent in = noti Intent(getActivity(), WRSListActivity.class);
                in.putExtra("Entered", entered);
                in.putExtra("Scheme", mSelectedScheme);
                startActivity(in);
               // getDateList(mSelectedScheme, tvdate.getText().toString(), "1", entered);
            }
        }
    }*/

    /*public void getSerialList(String sr_no, String scheme, String flag){
        if (selected == 3){
            Intent in = new Intent(getActivity(), WRSListActivity.class);
            in.putExtra("SR_No", sr_no);
            in.putExtra("Scheme", scheme);
            in.putExtra("Flag", flag);
            startActivity(in);
        }else {
            UserController userController = new UserController(getActivity(), "updateSerial");
            userController.getSerialReport(sr_no, scheme, flag);
        }
    }

    public void getMonthList(String scheme){
        UserController userController = new UserController(getActivity(), "updateMonth");
        userController.getMonthReport(scheme);
    }

    public void getDateList(String scheme, String date, String flag, String enteredBy){
        UserController userController = new UserController(getActivity(), "updateDate");
        userController.getDateReport(scheme, date, flag, enteredBy);
    }

    public void updateSerial(Object object) {
        mSerialReport = ((UserController) object).mSerialReport;
        if (mSerialReport != null) {
            mAdapterSerial = new SerialReportsAdapter(getActivity(), mSerialReport);
            listView.setAdapter(mAdapterSerial);

            if (listDate.isShown()){
                listDate.setVisibility(View.GONE);
                llListDate.setVisibility(View.GONE);
            }
        }
    }

    public void updateMonth(Object object) {
        mMonthReport = ((UserController) object).mMonthReport;
        if (mMonthReport != null) {
            mAdapter = new MonthReportsAdapter(getActivity(), mMonthReport);
            listView.setAdapter(mAdapter);
        }
    }

    public void updateDate(Object object) {
        mDateReport = ((UserController) object).mDateReport;
        if (mDateReport != null) {
            mAdapterDate = new DateReportsAdapter(getActivity(), mDateReport, mSelectedScheme);
            llListDate.setVisibility(View.VISIBLE);
            listDate.setVisibility(View.VISIBLE);
            listDate.setAdapter(mAdapterDate);
            llEntered.setVisibility(View.GONE);
            lldate.setVisibility(View.VISIBLE);
        }
    }*/

    /*public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
*/
    public void goBack(View v) {
        getActivity().finish();
    }
}
