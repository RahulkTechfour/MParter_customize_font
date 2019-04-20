package com.luminous.mpartner.connect;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.luminous.mpartner.BuildConfig;
import com.luminous.mpartner.activities.LoginActivity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class UserController {
    private Context mContext;
    public ArrayList<Scheme> mSchemeList;
    private String mMethodname;
    public ArrayList<Product> mWRSLabel;
    public boolean isSerialValid;
    public boolean disableSerialNoInput;
    public boolean showUploadImageOption;
    public String mMessage;
    public ArrayList<Dealer> mDealersList;
    public ArrayList<State> mStateList;
    public DealerAddress mDealerAddress;
    public ArrayList<City> mCityList;
    public ArrayList<ConnectDownloadPDF> mDownload;
    public ArrayList<ConnectReport> mReport;
    public ArrayList<TicketList> mTicketList;
    public ArrayList<ConnectPoint> mPoint;


    public UserController(Context context, String methodName) {
        mContext = context;
        mMethodname = methodName;
    }

    @SuppressWarnings("rawtypes")
    public Object invoke(Object... parameters)
            throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException {
        Class[] argumentTypes = {Object.class};
        Log.e("", "====> " + argumentTypes.length);
        Method method = mContext.getClass().getMethod(mMethodname,
                argumentTypes);
        return method.invoke(mContext, this);
    }


    public void getSchemes() {

        HashMap<String, String> properties = new HashMap<String, String>();

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mSchemeList = new ArrayList<Scheme>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            Scheme scheme = new Scheme(record
                                    .getProperty(UrlConstants.KEY_SCHEME_NAME)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty(UrlConstants.KEY_SCHEME_VALUE)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mSchemeList.add(scheme);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_GET_SCHEME);
    }

    public void getPageVisits(String pagename) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("userid",  new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.USER_ID));
        properties.put("pagename", pagename);
        properties.put("token", new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.TOKEN));
        properties.put("deviceid", CommonUtility.getDeviceId(mContext));
        properties.put("Appversion", BuildConfig.VERSION_NAME);
        properties.put("Ostype", "Android");
        properties.put("Osversion", Build.VERSION.RELEASE);
        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_COUNT_VISIT);
    }


    public void getWRSLabel() {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("userid",  new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.USER_ID));
        properties.put("token", new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.TOKEN));
        properties.put("Appversion", BuildConfig.VERSION_NAME);
        properties.put("deviceid", CommonUtility.getDeviceId(mContext));
        properties.put("Ostype", "Android");
        properties.put("Osversion", Build.VERSION.RELEASE);

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        mWRSLabel = new ArrayList<Product>();
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            Product label = new Product(record
                                    .getProperty("Id")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("FooterCatName")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mWRSLabel.add(label);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_WRS_LABEL);
    }

    public void getSerialNumberValidation(String serialNumber) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("sr_no",
                serialNumber);

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        String invalid = response
                                .getPropertyAsString(0);
                        if (invalid.contains("ok")) {
                            isSerialValid = true;
                            disableSerialNoInput = true;
                            showUploadImageOption = false;
                            mMessage = invalid;
                        } else if (invalid.contains("image_required")) {
                            isSerialValid = true;
                            disableSerialNoInput = true;
                            showUploadImageOption = true;
                            mMessage = invalid;
                        } else if (invalid.contains("Already_Entered")) {
                            isSerialValid = false;
                            disableSerialNoInput = false;
                            showUploadImageOption = false;
                            mMessage = invalid;
                        } else if (invalid.contains("Invalid")) {
                            isSerialValid = false;
                            disableSerialNoInput = false;
                            showUploadImageOption = false;
                            mMessage = invalid;
                        } else {
                            //  isSerialValid = true;
                        }

                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_SERIAL_EXISTANCCE);
    }

    public void getDistributerListByDealer(String dealerId) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("dlr_code",
                dealerId);


        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mDealersList = new ArrayList<Dealer>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            Dealer dealer = new Dealer(record
                                    .getProperty("dist_code")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("dist_name")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mDealersList.add(dealer);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_DISTRIBUTER_BY_DEALER_LIST);
    }

    public void getStateList() {

        HashMap<String, String> properties = new HashMap<String, String>();

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mStateList = new ArrayList<State>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            State state = new State(record
                                    .getProperty(UrlConstants.KEY_STATE_ID)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty(UrlConstants.KEY_STATE_NAME)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mStateList.add(state);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_STATE_LIST);
    }

    public void getCityList(String stateId) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("Stateid", stateId);

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mCityList = new ArrayList<City>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            City city = new City(record
                                    .getProperty(UrlConstants.KEY_DIST_ID)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty(UrlConstants.KEY_DIST_NAME)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mCityList.add(city);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_CITY_LIST);
    }

    public void getDealerAddress(String dealerId) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("DlrID", dealerId);

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        SoapObject ss = (SoapObject) resultObj.getProperty("StrssscDealerNameAddress");
                        if (ss != null) {
                            mDealerAddress = new DealerAddress(ss.getProperty(UrlConstants.KEY_DEALERADDRESS)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), ss.getProperty(UrlConstants.KEY_DEALERNAME)
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_DEALER_ADDRESS);
    }

    public void saveEntry(String SerialNo, String DisCode, String DlrCode, String SaleDate, String CustomerName,
                          String CustomerPhone, String CustomerLandLineNumber, String CustomerState, String CustomerCity, String CustomerAddress, String LogDistyId, String Mtype, String Image) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("SerialNo", SerialNo);
        properties.put("DisCode", DisCode);
        properties.put("DlrCode", DlrCode);
        properties.put("SaleDate", SaleDate);
        properties.put("CustomerName", CustomerName);
        properties.put("CustomerPhone", CustomerPhone);
        properties.put("CustomerLandLineNumber", CustomerLandLineNumber);
        properties.put("CustomerState", CustomerState);
        properties.put("CustomerCity", CustomerCity);
        properties.put("CustomerAddress", CustomerAddress);
        properties.put("LogDistyId", LogDistyId);
        properties.put("ismtype", Mtype);
        properties.put("img", Image);


        new CommonAsyncTask(mContext, properties, true) {


            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mMessage = response.getPropertyAsString(0);
                        Log.d("myTag", mMessage);
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_SAVE_ENTRY);
    }

    public void downloadPDF(String scheme, String type) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("dis_dlr_code", new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.USER_ID));
        properties.put("Scheme", scheme);
        properties.put("user_type", type);
        properties.put("serial_number", "");

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mDownload = new ArrayList<ConnectDownloadPDF>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            ConnectDownloadPDF report = new ConnectDownloadPDF(record
                                    .getProperty("Product_Serial_Number")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Product_Type")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Dis_Sap_Code")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Dis_Name")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Dlr_Sap_Code")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Dlr_Name")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("product_code")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("product_detail")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Sale_Date")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("cust_Name")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("cust_number")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("cust_add")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("dis_state")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("dlr_state")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Point")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Status")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Remark")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Created_On")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mDownload.add(report);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.DOWNLOAD_PDF);
    }

    public void getConnectReport(String scheme, String type) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("dist_code",  new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.USER_ID));
        properties.put("Scheme", scheme);
        properties.put("user_type", type);

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mReport = new ArrayList<ConnectReport>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            ConnectReport report = new ConnectReport(record
                                    .getProperty("Particulars")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("HUPS")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Battery")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("HKVA")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Panel")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Stabilizer")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mReport.add(report);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_REPORT_CONNECT);
    }

    public void getTicketList(String month, String year) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("userid",  new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.USER_ID));
        properties.put("month", month);
        properties.put("year", year);
        properties.put("token", new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.TOKEN));
        properties.put("deviceid", CommonUtility.getDeviceId(mContext));
        properties.put("Appversion", BuildConfig.VERSION_NAME);
        properties.put("Ostype", "Android");
        properties.put("Osversion", Build.VERSION.RELEASE);
        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mTicketList = new ArrayList<TicketList>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            if (record.getProperty("Date") != null) {
                                TicketList report = new TicketList(record
                                        .getProperty("Date")
                                        .toString()
                                        .replace(UrlConstants.KEY_ANY,
                                                UrlConstants.KEY_BLANK), record
                                        .getProperty("serialno")
                                        .toString()
                                        .replace(UrlConstants.KEY_ANY,
                                                UrlConstants.KEY_BLANK), record
                                        .getProperty("status")
                                        .toString()
                                        .replace(UrlConstants.KEY_ANY,
                                                UrlConstants.KEY_BLANK), record
                                        .getProperty("Id")
                                        .toString()
                                        .replace(UrlConstants.KEY_ANY,
                                                UrlConstants.KEY_BLANK));
                                mTicketList.add(report);
                            }
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.GET_TICKET_LIST);
    }

    public void getConnectReportPoint(String scheme, String type) {

        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("dist_code", new SharedPrefsManager(mContext).getString(SharedPreferenceKeys.USER_ID));
        properties.put("Scheme", scheme);
        properties.put("user_type", type);

        new CommonAsyncTask(mContext, properties, true) {

            @Override
            protected void onPostExecute(SoapObject response) {
                super.onPostExecute(response);
                if (response != null) {
                    try {
                        mPoint = new ArrayList<ConnectPoint>();
                        SoapObject resultObj = (SoapObject) response
                                .getProperty(0);
                        for (int i = 0; i < resultObj.getPropertyCount(); i++) {
                            SoapObject record = (SoapObject) resultObj
                                    .getProperty(i);
                            ConnectPoint report = new ConnectPoint(record
                                    .getProperty("Points_earned")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Current_Slab")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK), record
                                    .getProperty("Point_Next_Slab")
                                    .toString()
                                    .replace(UrlConstants.KEY_ANY,
                                            UrlConstants.KEY_BLANK));
                            mPoint.add(report);
                        }
                        invoke();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        invoke();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute(UrlConstants.METHOD_REPORT_CONNECT_POINT);
    }
}
