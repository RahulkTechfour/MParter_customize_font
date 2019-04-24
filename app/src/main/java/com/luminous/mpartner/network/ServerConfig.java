package com.luminous.mpartner.network;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.luminous.mpartner.BuildConfig;
import com.luminous.mpartner.sales.OrderEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.SharedPreferenceKeys;
import com.luminous.mpartner.utilities.SharedPrefsManager;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {

    public static final String BY_DEFALT = "/storage/emulated/img/...\"base64,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";
    public static String TEST_URL = "http://166.62.100.102/Api/MpartnerApi/";
    public static String PRODUCTION_URL = "http://166.62.100.102/Api/MpartnerApi/";
    public static String TEST_OTP_URL = "https://mpartnerv2.luminousindia.com/nonsapservices/api/nonsapservice/";
    public static String PRIMARY_REPORT_URL = "http://mpartner.luminousintranet.com:81/nonsapservices/api/sapservice/";

    // new url
//    public static String TEST_URL = "https://mpartnerv2.luminousindia.com/Api/MpartnerApi/";
//    public static String PRODUCTION_URL = "https://mpartnerv2.luminousindia.com/Api/MpartnerApi/";
//    public static String TEST_OTP_URL = "https://mpartnerv2.luminousindia.com/nonsapservices/api/nonsapservice/";
//    public static String PRIMARY_REPORT_URL = "https://mpartnerv2.luminousindia.com/nonsapservices/api/sapservice/";

    private static final String TAG = "ServerConfig";


    public static String getNotificationAlert() {
        return PRODUCTION_URL + "GetAlertnotification?";
    }

    public static String getURL() {
        if (BuildConfig.DEBUG)
            return TEST_URL;
        else
            return PRODUCTION_URL;
    }

    public static String getWRDealerSummary(String disCode, String dealerCode, String scheme) {
        return String.format(TEST_OTP_URL + "SerWrReportDlrWiseSchemeConnect?" +
                "dist_code=%s&" +
                "&dlr_code=%s&" +
                "Scheme=%s&" +
                "token=pass@1234" +
                "&way=m", disCode, dealerCode, scheme);
    }

    public static String getWRDealerSummaryWeb(String disCode, String dealerCode, String scheme) {
        return String.format(TEST_OTP_URL + "SerWrReportDlrWiseSchemeConnect?" +
                "dist_code=%s&" +
                "&dlr_code=%s&" +
                "Scheme=%s&" +
                "token=pass@1234" +
                "&way=w", disCode, dealerCode, scheme);
    }

    public static String getWRDealerList(String disCode) {
        return String.format(TEST_OTP_URL + "mSerWRDlrListByDist?" +
                "dist_code=%s&" +
                "token=pass@1234", disCode);
    }

    public static String getWRDistSummary(String disCode, String scheme, String userType) {
        return String.format(TEST_OTP_URL + "SerWRReportConnect_Card?" +
                "dist_code=%s&" +
                "Scheme=%s&" +
                "user_type=%s&" +
                "token=pass@1234", disCode, scheme, userType);
    }

    public static String getWRSchemeList() {
        return TEST_OTP_URL + "SerWRGet_Scheme_Name?token=pass@1234";
    }


    public static String get21DaysReportUrl(String from, String to) {
        return PRODUCTION_URL + "Distributor_Primary_Billing_Report?fromdate=" + from + "&todate=" + to;
    }

    public static String getSubmitSurveyResponse(String sId, String option, String optionText) {
        return PRODUCTION_URL + "SaveSurveyResult?" + String.format(
                "surveyid=%s&" +
                        "option=%s&" +
                        "optionvalue=%s", sId, option, optionText);
    }

    public static String getSurveyQuestion(String id) {
        return PRODUCTION_URL + "GetSurveyQuestion?Surveyid=" + id;
    }

    public static String getSurveyList() {
        return PRODUCTION_URL + "GetSurveyNotificationList?";
    }

    public static String getNotificationRead(String nId) {
        return PRODUCTION_URL + "IsReadCheck_AlertNotification?isread=1&notificationid=" + nId;
    }


    public static String getNotification() {
        return PRODUCTION_URL + "GetAlertnotification?";
    }

    public static String getMedialList() {
        return PRODUCTION_URL + "Media_maindata?";
    }

    public static String getSchemePointSlab(String discode, String userType) {
        return String.format("http://mpartner.luminousintranet.com:81/nonsapservices/api/nonsapservice/SerWRReportConnect2_Card_Reward_Ponits?" +
                "dist_code=%s0000891&" +
                "Scheme=connect%20plus%202&" +
                "user_type=%s&" +
                "token=pass@1234\n", discode, userType);
    }

    public static String getWRStatusReportCard(String discode, String userType) {
        return TEST_OTP_URL + "SerWRReportConnect_Card?" +
                "dist_code=" + discode +
                "&Scheme=connect%20plus%203" +
                "&user_type=" + userType +
                "&token=pass@1234";
    }

    public static String getCreateSalesOrder(String discode, String ch, String div, ArrayList<OrderEntity> entities) {

        String parameterMaterialNo = "Material[%d][MATERIAL_NO]=%s";
        String parameterMaterialQ = "Material[%d][QUANTITY]=%s";

        String res = "";

        for (int i = 0; i < entities.size(); i++) {
            OrderEntity entity = entities.get(i);
            res = res + String.format(parameterMaterialNo, i, entity.getpId()) + "&" +
                    String.format(parameterMaterialQ, i, entity.getpQty());

            if (i < entities.size() - 1) {
                res += "&";
            }
        }


        return String.format(PRIMARY_REPORT_URL + "CREATE_ORDER?" +
                "Discode=%s&" +
                "Channel=%s&" +
                "Division=%s&" +
                "token=pass@1234&", discode, ch, div) + res;
    }

    public static String getProductAndPDesc(String ch, String div) {
        return String.format(TEST_OTP_URL + "materiallist?" +
                "Channel=%s&" +
                "Division=%s&" +
                "token=pass@1234", ch, div);
    }

    public static String getDivisionList() {
        return TEST_OTP_URL + "DivisionList?token=pass@1234\n";
    }

    public static String getChannelList() {
        return TEST_OTP_URL + "ChannelList?token=pass@1234";
    }

    public static String getSalesViewOrder(String discode, String fromDate, String toDate) {
        return String.format(PRIMARY_REPORT_URL + "DISTRIBUTOR_SALES_ORDER?" +
                "Discode=%s&" +
                "FROMDATE=%s&" +
                "TODATE=%s&" +
                "token=pass@1234&" +
                "way=m\n", discode, fromDate, toDate);
    }

    public static String getEscalationMatrix() {
        return PRODUCTION_URL + "EscalationMatrix?";
    }

    public static String getDealerList(String status, String from, String to, String userId) {
        String url = String.format(PRIMARY_REPORT_URL + "DEALER_LIST_REPORT?" +
                "I_STAUTS=%s&" +
                "I_DATE_FROM=%s&" +
                "I_DATE_TO=%s&" +
                "I_LOGIN_ID=%s&" +
                "token=pass@1234&" +
                "way=m", status, from, to, userId);
        Log.d(TAG, "getDealerList: " + url);
        return url;
    }

    public static String getWRSaveEntryUpdatedSchemeImgpath() {
        return TEST_OTP_URL + "mSerWRSaveEntryUpdatedSchemeImgpath";
    }

    public static String getDealerList(String userId) {
        return String.format(TEST_OTP_URL + "mSerWRDlrListByDist?dist_code=%s&token=pass@1234", userId);
    }

    public static String getWRSrNoExistenceUpdateUrl(String serialNumber) {
        return String.format(TEST_OTP_URL + "mSerWRSrNoExistanceUpdate?sr_no=%s&token=pass@1234", serialNumber);
    }

    public static String getCreateDealerUrl() {

        return PRIMARY_REPORT_URL + "DEALER_CREATE?" +
                "Discode=%s&" +
                "DealerFirmName=%s&" +
                "OwnerName=%s&" +
                "Address1=%s&" +
                "Address2=%s&" +
                "City=%s" +
                "&District=Text" +
                "&State=%s" +
                "&PostalCode=%s&" +
                "Country=india&" +
                "Mobile=%s&" +
                "Telephone=%s&" +
                "EmailId=%s" +
                "&token=pass@1234";
    }

    public static String getStateUrl() {

        return TEST_OTP_URL + "sapStateList?token=pass@1234";
    }

    public static String getCityUrl() {
        return TEST_OTP_URL + "sapCityList?stateID=%s&token=pass@1234";
    }

    public static String getPrimaryBillingReportUrl(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "PRIMARY_RECV_REPORT?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=m";

    }

    public static String getPrimaryBillingReportUrlWeb(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "PRIMARY_RECV_REPORT?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=w";

    }

    public static String getCreditDebitUrl(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "DISTRIBUTOR_CRDR?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=m";
    }

    public static String getCreditDebitUrlWeb(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "DISTRIBUTOR_CRDR?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=w";
    }

    public static String getCustomerLedgerUrl(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "DISTRIBUTOR_LEDGER?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=m";
    }

    public static String getCustomerLedgerUrlWeb(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "DISTRIBUTOR_LEDGER?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=w";
    }

    public static String getProdcutReconciliationUrl(String discode, String serialNumber) {
        return TEST_OTP_URL + "SerWRS_Primary_Sec_Ter_Detail?DisCode=" + discode + "&SerialNo=" + serialNumber + "&token=pass@1234\n";
    }

    public static String getTEST_OTP_URL() {
        return TEST_OTP_URL;
    }

    public static String getHomePageDataUrl() {
        return getURL() + "home_page_cards";

//        return getURL() + "home_page_cards?token=e26aafe885163d9b565fe4b9dcad10f1&user_id=9900000002&app_version=3.9&appversion_code=28&device_id=3.0.0&device_name=iphone5&os_type=iOS&os_version_name=3.2.0&os_version_code=12&ip_address=166.62.100.102&language=EN&screen_name=dashboard&network_type=2G&network_operator=Airtel&time_captured=1550484272&channel=M";
    }

    public static String getSchemePageDataUrl() {
        return getURL() + "scheme_page_cards";
//        return getURL() + "scheme_page_cards?token=e26aafe885163d9b565fe4b9dcad10f1&user_id=9900000002&app_version=3.9&appversion_code=28&device_id=3.0.0&device_name=iphone5&os_type=iOS&os_version_name=3.2.0&os_version_code=12&ip_address=166.62.100.102&language=EN&screen_name=dashboard&network_type=2G&network_operator=Airtel&time_captured=1550484272&channel=M";
    }


    public static String getPriceListPageDataUrl() {
        return getURL() + "pricelist_page_cards?parentid=%s";
        //        return getURL() + "pricelist_page_cards?token=e26aafe885163d9b565fe4b9dcad10f1&user_id=9900000002&app_version=3.9&appversion_code=28&device_id=3.0.0&device_name=iphone5&os_type=iOS&os_version_name=3.2.0&os_version_code=12&ip_address=166.62.100.102&language=EN&screen_name=dashboard&network_type=2G&network_operator=Airtel&time_captured=1550484272&channel=M";
    }

    public static String getPriceListHeaderURL() {
        return getURL() + "ParentCategory";
//        return "http://166.62.100.102/Api/MpartnerApi/ParentCategory?user_id=9999998&token=9714e77c48ecc0a5c6cb5d50c9ae68d7&app_version=3.9&device_id=3c83daae90bfe9d5&device_name=Oppo&os_type=Android&os_version_name=Noughat&os_version_code=5.1.1&ip_address=192.98.89&language=EN&screen_name=A.class&network_type=2g&network_operator=Airtel&time_captured=123213123123123&channel=M";
    }

    public static String getCatalogMenuFooterUrl() {
        return getURL() + "Catalog_menu_footer";
    }

    public static String getCatalogMenuHeaderUrl() {
        return getURL() + "Catalog_menu_upper?productcategoryid=%s";
    }

    public static String getCatalogProductsUrl() {
        return getURL() + "Catalog_products?productcategoryid=%s";
    }

    public static String getCatalogProductsDetailsUrl() {
        return getURL() + "Catalog_products_details?catalogid=%s";
    }


    public static String getSearchProductUrl() {
        return getURL() + "Search_products?search_key=%s";
    }


    public static String getContactUsDetailsUrl() {
        return getURL() + "Contactus_details";
    }

    public static String saveContactUsSuggestionUrl() {
        return getURL() + "Save_contactus_suggestion";
    }

    public static String saveAssistTicket() {
        return getURL() + "SaveTicket";
    }

    public static String updateAssistTicket() {
        return getURL() + "UpdateTicket";
    }

    public static String getFaqDataUrl() {
        return getURL() + "Faq_data";
    }

    public static String getGalleryMainDataUrl() {
        return getURL() + "Gallery_maindata?gallery_categoryid=%s";
    }

    public static String getGalleryUpperMenuUrl() {
        return getURL() + "Gallery_menu_upper";
    }

    public static String getDistributorCountUrl() {
        return getURL() + "LSD_GetDistributorCount";
    }

    public static String saveQRCodeUrl(String qrCode) {
        return getURL() + "LSD_SaveQrCode?qrcode=" + qrCode;
    }

    public static String getDistOpenReimbursementReportUrl() {
        return getURL() + "LSD_GetDistOpenReimbursmentReport";
    }

    public static String getGoldDistOpenReimbursementReportUrl() {
        return getURL() + "LSD_Gold_GetActivatedCouponReport";
    }

    public static String getRedeemedReportUrl() {
        return getURL() + "LSD_GetDistOpenReimbursmentReport";
    }

    public static String getActivatedCouponReportUrl() {
        return getURL() + "LSD_GetActivatedCouponReport";
    }

    public static String saveClaimDataUrl(String userId, String token, String appVersion, String deviceId, String deviceName, String osType, String os_version_name, String OsVersionCode, String ipAddress, String lang, String screen_name, String networkType, String netOperator, String timeStamp, String channel) {
        return getURL() + "LSD_SaveClaimData?user_id=" + userId + "&token=" + token + "&app_version=" + appVersion + "&device_id=" + deviceId + "&device_name=" + deviceName + "&os_type=" + osType + "&os_version_name=" + os_version_name + "&os_version_code=" + OsVersionCode + "&ip_address=" + ipAddress + "&language=" + lang + "&screen_name=" + screen_name + "&network_type=" + networkType + "&network_operator=" + netOperator + "&time_captured=" + timeStamp + "&channel=" + channel;
    }

    public static String getDealerReportUrl(String secretcode, String barcode) {
        return getURL() + "LSD_DealerReport?secretcode=" + secretcode + "&barcode=" + barcode;
    }

    public static String saveDealerScanCodeUrl(String barCode, String secretcode) {
        return getURL() + "LSD_SaveDealerScanCode?barcode=" + barCode + "&secretcode=" + secretcode;
    }

    public static String getCustomerPermissionDataUrl() {
        return getURL() + "Customer_permission_data";
    }

    public static String createOTPUrl() {
        return getTEST_OTP_URL() + "sscMDisCreateOtp?DisID=%s&imeinumber=%s&osversion=%s&devicename=%s&appversion=%s&token=pass@1234";
    }

    public static String verifyOTPUrl() {
        return getTEST_OTP_URL() + "sscMDisVarifyOtp?DisID=%s&imeinumber=%s&osversion=%s&devicename=%s&otp=%s&token=pass@1234";
    }

    public static String userverificationURL() {
        return getURL() + "Userverification";

    }

    public static String GetWRSLabelsURL() {
        return getTEST_OTP_URL() + "GetWRSLabels";
    }

    public static String mSerWRSrNoExistanceUpdate(String sr_no) {
        return getTEST_OTP_URL() + "mSerWRSrNoExistanceUpdate?sr_no=" + sr_no + "&token=pass@1234";
    }

    public static String mSerWRDlrListByDist(String dist_code) {
        return getTEST_OTP_URL() + "mSerWRDlrListByDist?dist_code=" + dist_code;
    }

    public static String mSerWRDistListByDlr(String dlrCode) {
        return getTEST_OTP_URL() + "mSerWRDistListByDlr?dlr_code=" + dlrCode + "&token=pass@1234";
//        return TEST_OTP_URL + "mSerWRDistListByDlr?dlr_code=%s&token=pass@1234";
    }

    public static String SerWRStateList() {
        return getTEST_OTP_URL() + "SerWRStateList?token=pass@1234";
    }

    public static String SerWRGetCityName(String stateId) {
        return getTEST_OTP_URL() + "SerWRGetCityName?Stateid=" + stateId + "&token=pass@1234";
    }

    public static String mSerWRSaveEntryUpdatedSchemeImgpath(String SerialNo, String DisCode, String DlrCode, String saleDate,
                                                             String name, String phoneNumber, String landLine, String state, String city,
                                                             String address, String logDistyId, String mType, String img) {
        return getTEST_OTP_URL() + "mSerWRSaveEntryUpdatedSchemeImgpath?" +
                "SerialNo=" + SerialNo + "&DisCode=" + DisCode + "&DlrCode=" + DlrCode + "&" +
                "SaleDate=" + saleDate + "&CustomerName=" + name + "&CustomerPhone=" + phoneNumber + "&" +
                "CustomerLandLineNumber=" + landLine + "&CustomerState=" + state + "&" +
                "CustomerCity=" + city + "&CustomerAddress=" + address + "&LogDistyId=" + logDistyId + "&ismtype=" + mType + "&img=" + img;
    }

    public static String SerWRReportConnect2_Card_Reward_Ponits(String distCode, String userType) {
        return getTEST_OTP_URL() + "SerWRReportConnect2_Card_Reward_Ponits?dist_code=" + distCode + "&Scheme=connect%20plus%202&user_type=" + userType + "&token=pass@1234";
    }

    public static String SStrSerWRReportConnect_Rejection_per(String distCode, String userType) {
        return getTEST_OTP_URL() + "StrSerWRReportConnect_Rejection_per?dist_code=" + distCode + "&Scheme=connect%20plus%202&user_type=" + userType + "&token=pass@1234";
//        return getTEST_OTP_URL() + "StrSerWRReportConnect_Rejection_per?dist_code=" + distCode + "&Scheme=connect+2&user_type=" + userType;
    }

    public static String sscPartnerDetailst(String SessionLoginDisID) {
        return getTEST_OTP_URL() + "sscPartnerDetailst?SessionLoginDisID=" + SessionLoginDisID + "&token=pass@1234";
    }

//    public static String SerWRReportConnect_Card(String dist_code, String user_type) {
//        return getTEST_OTP_URL() + "SerWRReportConnect_Card?dist_code=" + dist_code + "&Scheme=connect+1&user_type=" + user_type;
//    }

    public static String getConnectCards() {
        return getURL() + "connectplus_page_cards";
    }

    public static String getTicketList(String month, String year) {
        return getURL() + "GetTicketList?month=" + month + "&year=" + year;
    }

    public static String getViewTicket(String ticketId) {
        return getURL() + "ViewTicket?ticketid=" + ticketId;
    }

    public static String saveConnectData() {
        return getURL() + "Save_ConnectPlusDataEntry";
    }


    public static String getItemTypeURL() {
        return getTEST_OTP_URL() + "sscItemList?Flag=All&token=pass@1234";
//        return TEST_OTP_URL + "sscItemList?Flag=All&token=pass@1234";
    }

    public static String getPrimaryReport(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "PRIMARY_RECV_REPORT?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=m";
    }

    public static String getPrimaryReportWeb(String discode, String fromDate, String toDate) {
        return PRIMARY_REPORT_URL + "PRIMARY_RECV_REPORT?Discode=" + discode + "&FROMDATE=" + fromDate + "&TODATE=" + toDate + "&token=pass@1234&way=w";
    }


    public static String getSecSaleQuantitySummary() {
        return TEST_OTP_URL + "sscAvailableStockSummary?SessionLoginDisID=%s&token=pass@1234";
    }

    public static String getSecondarySalesReport() {
        return TEST_OTP_URL + "sscDispatchableStockListPopUp?SessionLoginDisID=%s&ItemID=%s&token=pass@1234";
    }

    public static String getSecondaryDispatchReport(String discode, String dlrId, String itemId, String fromDate, String toDate) {
        return String.format(TEST_OTP_URL + "sscDistachedReportDataPopUp?" +
                "SessionLoginDisID=%s&" +
                "DlrID=%s&" +
                "ItemID=%s&" +
                "DispatchedDateFrom=%s&" +
                "DispatchedDateTo=%s" +
                "&token=pass@1234&way=m", discode, dlrId, itemId, fromDate, toDate);
    }

    public static String getSecondaryDispatchReportWeb(String discode, String dlrId, String itemId, String fromDate, String toDate) {
        return String.format(TEST_OTP_URL + "sscDistachedReportDataPopUp?" +
                "SessionLoginDisID=%s&" +
                "DlrID=%s&" +
                "ItemID=%s&" +
                "DispatchedDateFrom=%s&" +
                "DispatchedDateTo=%s" +
                "&token=pass@1234&way=w", discode, dlrId, itemId, fromDate, toDate);
    }

//    public static String getPrimaryReport() {
//        return getTEST_OTP_URL() + "PRIMARY_RECV_REPORT?Discode=%s&FROMDATE=%s&TODATE=%s&way=m";
//    }


    public static String newUrl(String url, Context context, String callerClass) {

        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        String userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);

        //user Id
        if (url.contains("?"))
            url += "&user_id=" + userId;
        else
            url += "?user_id=" + userId;
//


        //Token
        if (!TextUtils.isEmpty(sharedPrefsManager.getString(SharedPreferenceKeys.TOKEN)))
            url += "&token=" + sharedPrefsManager.getString(SharedPreferenceKeys.TOKEN);
//
//        if (url.contains("?")) {
//            url += "&user_id=9900000002&token=e26aafe885163d9b565fe4b9dcad10f1";
//        } else {
//            url += "?user_id=9900000002&token=e26aafe885163d9b565fe4b9dcad10f1";
//        }

        //app version name and code
        String appVersionName = "";
        String appVersionCode = "";
        final List<String> versionList = CommonUtility.appVersionDetails(context);
        if (versionList != null && versionList.size() == 2) {
            appVersionName = versionList.get(0);
            appVersionCode = versionList.get(1);
        }
        url += "&app_version=" + appVersionName;
        url += "&appversion_code=" + appVersionCode;

        //device_id
        url += "&device_id=" + CommonUtility.getDeviceId(context);

        //device_name
        url += "&device_name=" + CommonUtility.getDeviceName();

        //os_type
        url += "&os_type=Android";

        //os_version_code and os_version_name
        if (!url.toLowerCase().contains("os_v_code") && !url.toLowerCase().contains("os_v_code")) {
            try {
                url += "&os_version_code=" + Build.VERSION.SDK_INT;
                url += "&os_version_name=" + Build.VERSION.RELEASE;
            } catch (Exception e) {
                // intentionally left blank
            }
        }

        //ip_address
        url += "&ip_address=" + CommonUtility.getIpAddress(context);

        //language=EN
        url += "&language=" + CommonUtility.getAppLanguage(context);

        //screen_name=A.class
        if (callerClass != null) {
            url += "&screen_name=" + callerClass;
        }

        //network_type=2G
        url += "&network_type=" + CommonUtility.getNetworkType(context);

        //network_operator=Airtel
        url += "&network_operator=" + CommonUtility.getNetworkOperator(context);

        //time_captured
        url += "&time_captured=" + System.currentTimeMillis();

        //channel=M
        url += "&channel=M";

        url += "&fcm_token=" + sharedPrefsManager.getString(SharedPreferenceKeys.FCM_TOKEN);

        url = url.replaceAll(" ", "%20");

        Log.d(TAG, "newUrl: " + url);

        return url;

    }

    public static String newUrl2(String url, Context context, String callerClass) {

        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
        String userId = sharedPrefsManager.getString(SharedPreferenceKeys.USER_ID);

        //user Id
        if (url.contains("?"))
            url += "&user_id=" + userId;
        else
            url += "?user_id=" + userId;
//


        //Token
        if (!TextUtils.isEmpty(sharedPrefsManager.getString(SharedPreferenceKeys.TOKEN)))
            url += "&token_m=" + sharedPrefsManager.getString(SharedPreferenceKeys.TOKEN);
//
//        if (url.contains("?")) {
//            url += "&user_id=9900000002&token=e26aafe885163d9b565fe4b9dcad10f1";
//        } else {
//            url += "?user_id=9900000002&token=e26aafe885163d9b565fe4b9dcad10f1";
//        }

        //app version name and code
        String appVersionName = "";
        String appVersionCode = "";

        final List<String> versionList = CommonUtility.appVersionDetails(context);
        if (versionList != null && versionList.size() == 2) {
            appVersionName = versionList.get(0);
            appVersionCode = versionList.get(1);
        }
        url += "&app_version=" + appVersionName;
        url += "&appversion_code=" + appVersionCode;
        url += "&token=pass@1234";
        url += "&way=w";

        //device_id
        url += "&device_id=" + CommonUtility.getDeviceId(context);

        //device_name
        url += "&device_name=" + CommonUtility.getDeviceName();

        //os_type
        url += "&os_type=Android";

        //os_version_code and os_version_name
        if (!url.toLowerCase().contains("os_v_code") && !url.toLowerCase().contains("os_v_code")) {
            try {
                url += "&os_version_code=" + Build.VERSION.SDK_INT;
                url += "&os_version_name=" + Build.VERSION.RELEASE;
            } catch (Exception e) {
                // intentionally left blank
            }
        }

        //ip_address
        url += "&ip_address=" + CommonUtility.getIpAddress(context);

        //language=EN
        url += "&language=" + CommonUtility.getAppLanguage(context);

        //screen_name=A.class
        if (callerClass != null) {
            url += "&screen_name=" + callerClass;
        }

        //network_type=2G
        url += "&network_type=" + CommonUtility.getNetworkType(context);

        //network_operator=Airtel
        url += "&network_operator=" + CommonUtility.getNetworkOperator(context);

        //time_captured
        url += "&time_captured=" + System.currentTimeMillis();

        //channel=M
        url += "&channel=M";

        //url += "&fcm_token=d9k1J7jXHGc:APA91bGGa0zsJa06YbApFg_M5F4wuzQr4ithDItUhu3CW4iSsTGsK-pImqhxqPgSO4HFrYaEUx1Ezpk74WupfGCu7m6iyQekU444RbTSthx0rQer7d-z6Zm_kDsS0UOfvWq15dUu1jr5";

        url = url.replaceAll(" ", "%20");

        Log.d(TAG, "newUrl: " + url);

        return url;

    }


}
