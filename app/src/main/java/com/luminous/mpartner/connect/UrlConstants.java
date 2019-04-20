package com.luminous.mpartner.connect;


public class UrlConstants {

    public static final String NAMESPACE = "http://tempuri.org/";
    public static final String URL = "http://sapservice.luminousindia.com/Lumb2bWebService/service.asmx";
    public static final String URL1 = "http://mpartner.luminousintranet.com:81/WebServices/GetBanner.asmx";
    public static final String URL2 = "http://mpartner.luminousintranet.com:81/WebServices/GetBanner.asmx";
//    public static final String URL1 = "http://166.62.100.102/webservices/getbanner.asmx";
//    public static final String URL2 = "http://166.62.100.102/webservices/getbanner.asmx";


    // KEYS
    public static final String KEY_CODE = "Code";


    public static final String KEY_DESCRIPTION = "des";
    public static final String KEY_ANY = "anyType{}";
    public static final String KEY_SLASH = "-";
    public static final String KEY_BLANK = "";

    // STATUS
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    // CREATE OTP
    public static final String METHOD_CREATE_OTP = "sscMDisCreateOtp";

    // VERIFY OTP
    public static final String METHOD_VALIDATE_OTP = "sscMDisVarifyOtp";

    // DEALER LIST
    public static final String METHOD_DEALER_LIST = "sscDealerList";
    public static final String KEY_DEALERCODE = "DlrCode";
    public static final String KEY_DEALERNAME = "DlrName";

    // DEALER DETAIL
    public static final String METHOD_DEALER_ADDRESS = "sscDealerNameAddress";
    public static final String KEY_DEALERADDRESS = "DlrAddress";

    // ACCESS CODES
    public static final String METHOD_ACCESS_CODE = "sscDispatchableStockListWithAccessCode";
    public static final String KEY_SKUMODELID = "SKUModelId";
    public static final String KEY_ACCESSCODE = "AccessCode";
    public static final String KEY_SKUMODELNAME = "SKUModelName";
    public static final String KEY_QUANTITY = "TAvlQuantity";

    // SUBMIT ORDER
    public static final String METHOD_SUBMIT_ORDER = "sscMInsertDispatchStockWithSerialNoModified";
    public static final String METHOD_SERIAL_EXISTANCCE = "mSerWRSrNoExistanceUpdate";
    public static final String METHOD_DEALER_BY_DISTRIBUTER_LIST = "mSerWRDlrListByDist";
    public static final String METHOD_DISTRIBUTER_BY_DEALER_LIST = "mSerWRDistListByDlr";
    public static final String METHOD_STATE_LIST = "SerWRStateList";
    public static final String METHOD_CITY_LIST = "SerWRGetCityName";
    public static final String METHOD_SAVE_ENTRY = "mSerWRSaveEntryUpdatedSchemeImgpath";
    public static final String METHOD_REPORTS = "mSerSchemeWisePayout";
    public static final String METHOD_GET_PROFILE = "GetProfile";
    public static final String METHOD_UPDATE_PROFILE = "UpdateProfile";
    public static final String METHOD_GET_BANNER = "GetBannerImage";

    /*****************changes by shashank*************/
    public static final String METHOD_GET_CONTEXT_PICTURE_FLAG = "GetContestPictureFlag";
    public static final String METHOD_GET_NAME_BY_SAP_CODE = "GetNameBySapcode";
    public static final String METHOD_GET_DEALER_IMAGE_COUNT = "GetDealerImageCount";

    public static final String METHOD_SAVE_DEL_CONTEST_PICTURE = "SaveDelContestPicture";
    public static final String METHOD_GET_CONTEXT_POLL_QUESTION = "GetContestPollQuestion";
    public static final String METHOD_SAVE_POLL_QUESTIONS = "SavePollQuestion";
    public static final String METHOD_GET_CONTEST_INFO_DATE = "GetInfo_Date";
    public static final String METHOD_GET_CONTEST_INFO_DATA = "GetContestInfodata";

    public static final String METHOD_GET_PRODUCTS = "getProducts";
    public static final String METHOD_GET_PRODUCT_LEVEL_ONE = "GetProductLevelOne";
    public static final String METHOD_GET_PRODUCT_LEVEL_TWO = "GetProductLevelTwo";
    public static final String METHOD_GET_PRODUCT_LEVEL_THREE = "GetProductLevelThreeVersion2";  //By Anusha
    //   public static final String METHOD_GET_PRODUCT_LEVEL_FOUR = "GetProductLevelFour";
    public static final String METHOD_GET_PRODUCT_DETAIL = "GetFullProductDetailVersion2";  //By Anusha
    public static final String METHOD_GET_PROMOTION_TYPE = "GetPermotionType";
    public static final String METHOD_GET_PROMOTION_DETAIL = "GetPromotions";
    public static final String METHOD_GET_SCHEME = "SerWRGet_Scheme_Name";
    public static final String METHOD_GET_CONTACT_US = "GetContact";
    public static final String METHOD_GET_NOTIFICATIONS = "GetNotificaions";
    public static final String METHOD_GET_SEARCH = "GetProductLevelFourByNameVersion2";  //By Anusha
    public static final String METHOD_GET_FUNCTIONS = "GetFunctionName";
    public static final String METHOD_GET_SUGGESTION = "Suggestion";
    public static final String METHOD_UPDATE_DEVICE_ID = "UpdateDeviceId";   //By Anusha

    //DEALER
    public static final String KEY_DEALER_CODE = "dlr_code";
    public static final String KEY_DEALER_NAME = "dlr_name";

    public static final String KEY_STATE_ID = "StateId";
    public static final String KEY_STATE_NAME = "StateName";
    public static final String KEY_DIST_ID = "DistId";
    public static final String KEY_DIST_NAME = "DistName";
    public static final String KEY_SCHEME_VALUE = "scheme_name";
    public static final String KEY_SCHEME_NAME = "scheme_value";

    public static final String METHOD_GET_MONTH_REPORT = "mSerRptSummaryMonthWise";
    public static final String METHOD_GET_DATE_REPORT = "mSerRptSummaryDateWise";
    public static final String METHOD_GET_SERIAL_REPORT = "mSerRptSerailNoDetail";
    public static final String METHOD_GET_DIS_DLR_LIST = "mSerDisDlrListScheme";
    public static final String METHOD_USER_WISE_SUMMARY = "mSerSummaryUserWise";

    //Contest web-services
    public static final String METHOD_INSERT_CONTEST = "InsertContest";
    public static final String METHOD_GET_CONTEST = "GetContestData";
    public static final String METHOD_UPDATE_CONTEST = "UpdateContest";

    public static final String METHOD_LEVEL_THREE_DATA = "SearchProductLevelThree";
    public static final String METHOD_GET_LUMINOUS_UPDATE = "getLuminiousUpdate";

    public static final String METHOD_USER_VERIFICATION = "Userverification";
    public static final String METHOD_USER_PERMISSIONS = "getCustomerPermission";
    public static final String METHOD_WRS_LABEL = "GetWRSLabels";
    public static final String METHOD_MEDIA_LABEL = "GetMediaLabels";
    public static final String METHOD_MEDIA_VIDEO = "GetProductVideoList";
    public static final String METHOD_GET_SURVEY_LIST = "GetSurveyNotificationList";
    public static final String METHOD_GET_SURVEY_QUESTION = "GetSurveyQuestion";
    public static final String METHOD_SAVE_SURVEY = "SaveSurveyResult";
    public static final String METHOD_FORCED_ALERTS = "GetAlertnotification";
    public static final String METHOD_ALERT_READ = "IsReadCheck";

    public static final String METHOD_REDIRECT_PAGE = "getRedirectPageData";
    public static final String METHOD_REDIRECT_PAGE_UPDATES = "LuminousUpdatesRedirectPageData";

    public static final String BY_DEFALT = "/storage/emulated/img/...\"base64,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";


    public static final String METHOD_GET_GALLERY = "GetGalleryData";

    public static final String METHOD_STATE = "GetState";
    public static final String METHOD_CITY = "GetTown";

    public static final String METHOD_CATEGORY_NAME = "GetParentCategoryData";

    public static final String METHOD_WRS_DATE = "WRS_Date";

    public static final String METHOD_APP_EXCEPTION = "App_Exception";

    //Connect+ Report by Anusha 31/05/2018 TASK#3783
    public static final String METHOD_REPORT_CONNECT = "SerWRReportConnect_Card";
    //Connect+ Report by Anusha 4/06/2018 TASK#3783
    public static final String METHOD_REPORT_CONNECT_POINT = "SerWRReportConnect_Card_Reward_Ponits";

    //Page visit count by Anusha 21/06/2018 TASK#3967
    public static final String METHOD_COUNT_VISIT = "CountSalesConnectVisit";

    //Create New Ticket by Anusha 27/06/2018 TASK#4009
    public static final String METHOD_SAVE_TICKET = "SaveTicket";

    //Get Tickets by Anusha 27/06/2018 TASK#4009
    public static final String GET_TICKET_LIST = "GetTicketList";

    //Get Ticket Detail by Anusha 27/06/2018 TASK#4009
    public static final String GET_TICKET_DETAIL = "ViewTicketEntity";

    //Get Ticket Detail by Anusha 06/07/2018 TASK#4009
    public static final String UPDATE_TICKET = "UpdateTicket";

    //Download PDF for Connect+ Report by Anusha 28/06/2018 TASK#4009
    public static final String DOWNLOAD_PDF = "mSerConnect_RptCardDetail";

    public static final String LSD_GetDistributorCount = "LSD_GetDistributorCount";
    public static final String LSD_GetClaimReport = "LSD_GetClaimReport";
    public static final String LSD_SaveQrCode = "LSD_SaveQrCode";
    public static final String LSD_SaveClaimData = "LSD_SaveClaimData";
    public static final String LSD_DealerReport = "LSD_DealerReport";
    public static final String LSD_SaveDealerScanCode = "LSD_SaveDealerScanCode";
    public static final String LSD_GetActivatedCouponReport = "LSD_GetActivatedCouponReport";
    public static final String LSD_GetDistOpenReimbursmentReport = "LSD_GetDistOpenReimbursmentReport";
    public static final String LSD_GetRedeemedReport = "LSD_GetRedeemedReport";
    public static final String LSD_TermsConditionInfo = "LSD_TermsConditionInfo";
    public static final String METHOD_SAVE_DEL_CONTEST_PICTURE_GEO_TAG = "SaveDelContestPictureGeoTag";
}
