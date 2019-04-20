package com.luminous.mpartner.network;

import com.luminous.mpartner.dynamic_home.entities.HomeData;
import com.luminous.mpartner.dynamic_home.entities.PriceData;
import com.luminous.mpartner.dynamic_home.entities.SchemeData;
import com.luminous.mpartner.network.entities.ActivatedCouponReportEntity;
import com.luminous.mpartner.network.entities.CatalogMenuFooterData;
import com.luminous.mpartner.network.entities.CatalogProductsEntity;
import com.luminous.mpartner.network.entities.CatalogUpperMenuEntity;
import com.luminous.mpartner.network.entities.ConnectEntryEntity;
import com.luminous.mpartner.network.entities.ContactEntity;
import com.luminous.mpartner.network.entities.ContactUsDetail;
import com.luminous.mpartner.network.entities.CreateOtpEntity;
import com.luminous.mpartner.network.entities.CustomerPermissionEntity;
import com.luminous.mpartner.network.entities.DealerReportEntity;
import com.luminous.mpartner.network.entities.FaqData;
import com.luminous.mpartner.network.entities.GalleryMainData;
import com.luminous.mpartner.network.entities.GalleryUpperMenuData;
import com.luminous.mpartner.network.entities.GetDistributorCount;
import com.luminous.mpartner.network.entities.GoldActivatedCouponReportEntity;
import com.luminous.mpartner.network.entities.OpenReimbursementReportEntity;
import com.luminous.mpartner.network.entities.PriceListHeaderEntity;
import com.luminous.mpartner.network.entities.ProductCatalogDetailEntity;
import com.luminous.mpartner.network.entities.RedeemReportEntity;
import com.luminous.mpartner.network.entities.SaveAssistEntityResponse;
import com.luminous.mpartner.network.entities.SaveConnectEntryEntity;
import com.luminous.mpartner.network.entities.SaveContactUsSuggestionResponse;
import com.luminous.mpartner.network.entities.SaveDealerSacnCodeEntity;
import com.luminous.mpartner.network.entities.SearchResponseEntity;
import com.luminous.mpartner.network.entities.SubmitResponse;
import com.luminous.mpartner.network.entities.TicketEntity;
import com.luminous.mpartner.network.entities.TicketListEntity;
import com.luminous.mpartner.network.entities.VerifyOtpEntity;
import com.luminous.mpartner.network.entities.ViewTicketEntity;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface RetrofitInterface {
    @GET
    Observable<HomeData> getHomePageData(@Url String url);

    @GET
    Observable<SchemeData> getSchemeData(@Url String url);

    @GET
    Observable<PriceData> getPriceListData(@Url String url);

    @GET
    Observable<PriceListHeaderEntity> getPriceListHeader(@Url String url);

    @GET
    Observable<CatalogMenuFooterData> getCatalogMenuFooterData(@Url String url);

    @GET
    Observable<CatalogUpperMenuEntity> getCatalogMenuUpperMenuData(@Url String url);

    @GET
    Observable<CatalogProductsEntity> getCatalogProducts(@Url String url);

    @GET
    Observable<ProductCatalogDetailEntity> getCatalogProductsDetails(@Url String url);

    @GET
    Observable<ContactUsDetail> getContactUsDetail(@Url String url);

    @POST
    Observable<SaveContactUsSuggestionResponse> saveContactUsSuggestion(@Url String url, @Body ContactEntity entity);

    @POST
    Observable<ResponseBody> saveAssistTicket(@Url String url, @Body TicketEntity entity);

    @POST
    Observable<ResponseBody> updateAssistTicket(@Url String url, @Body TicketEntity entity);

    @GET
    Observable<FaqData> getFaqData(@Url String url);

    @GET
    Observable<GalleryMainData> getGalleryMainData(@Url String url);

    @GET
    Observable<GalleryUpperMenuData> getGalleryUpperMenuData(@Url String url);

    @GET
    Observable<GetDistributorCount> getDistributorCountData(@Url String url);

    @GET
    Observable<SubmitResponse> saveQrCOde(@Url String url);

    @GET
    Observable<OpenReimbursementReportEntity> getOpenReimbursementReportData(@Url String url);

    @GET
    Observable<RedeemReportEntity> getRedeemReportData(@Url String url);

    @GET
    Observable<SubmitResponse> saveClaimData(@Url String url);

    @GET
    Observable<ActivatedCouponReportEntity> getActivatedCouponReportData(@Url String url);

    @GET
    Observable<GoldActivatedCouponReportEntity> getGoldActivatedCouponReportData(@Url String url);

    @GET
    Observable<DealerReportEntity> getDealerReportData(@Url String url);

    @GET
    Observable<SaveDealerSacnCodeEntity> saveDealerScanCode(@Url String url);

    @GET
    Observable<CustomerPermissionEntity> getCustomerPermissionData(@Url String url);

    @GET
    Observable<CreateOtpEntity> createOtp(@Url String url);

    @GET
    Observable<VerifyOtpEntity> verifyOtp(@Url String url);

    @GET
    Observable<ResponseBody> mSerWRSrNoExistanceUpdate(@Url String url);

    @GET
    Observable<ResponseBody> mSerWRDlrListByDist(@Url String url);

    @GET
    Observable<ResponseBody> mSerWRDistListByDlr(@Url String url);

    @GET
    Observable<ResponseBody> SerWRStateList(@Url String url);

    @GET
    Observable<ResponseBody> SerWRGetCityName(@Url String url);

    @GET
    Observable<SubmitResponse> mSerWRSaveEntryUpdatedSchemeImgpath(@Url String url);

    @GET
    Observable<ResponseBody> SerWRReportConnect2_Card_Reward_Ponits(@Url String url);

    @GET
    Observable<ResponseBody> SStrSerWRReportConnect_Rejection_per(@Url String url);

    @GET
    Observable<ResponseBody> SerWRReportConnect_Card(@Url String url);

    @GET
    Observable<ResponseBody> sscPartnerDetailst(@Url String url);

    @GET
    Observable<HomeData> getConnectCardData(@Url String url);

    @GET
    Observable<TicketListEntity> getTicketListData(@Url String url);

    @GET
    Observable<ViewTicketEntity> getTicketDetails(@Url String url);

    @GET
    Observable<ResponseBody> getItemTypeData(@Url String url);

    @GET
    Observable<ResponseBody> getPrimaryReport(@Url String url);

    @GET
    Observable<ResponseBody> getSecondarySaleQuantitySummaryReport(@Url String url);

    @GET
    Observable<ResponseBody> getSecondarySaleReport(@Url String url);

    @GET
    Observable<ResponseBody> getSecondaryDispatchReport(@Url String url);

    @GET
    Observable<ResponseBody> getProductReconciliationReport(@Url String url);

    @GET
    Observable<ResponseBody> getCustomerLedgerReport(@Url String url);

    @GET
    Observable<ResponseBody> getCreditDebitReport(@Url String url);

    @GET
    Observable<ResponseBody> getPrimaryBillingReport(@Url String url);

    @GET
    Observable<ResponseBody> getToken(@Url String url);

    @GET
    Observable<ResponseBody> getWRSLabel(@Url String url);

    @GET
    Observable<ResponseBody> getStateList(@Url String url);

    @GET
    Observable<ResponseBody> getCityList(@Url String url);

    @GET
    Observable<ResponseBody> getCreateDealer(@Url String url);

    @GET
    Observable<ResponseBody> getWRSrNoExistanceUpdate(@Url String url);

    @GET
    Observable<ResponseBody> getDealerList(@Url String url);

    @POST
    Observable<ResponseBody> getWRSaveEntryUpdatedSchemeImgpath(@Url String url, @Body ConnectEntryEntity connectEntryEntity);

    // change by Rahul
    @POST
    Observable<ResponseBody> getWRSaveEntryUpdatedSchemeImgpath_(@Url String url, @Body SaveConnectEntryEntity connectEntryEntity);



    @GET
    Observable<ResponseBody> getViewDealerList(@Url String url);

    @GET
    Observable<ResponseBody> getEscalationMatrix(@Url String url);

    @GET
    Observable<ResponseBody> getSalesViewOrder(@Url String url);

    @GET
    Observable<ResponseBody> getChannelList(@Url String url);

    @GET
    Observable<ResponseBody> getDivisonList(@Url String url);

    @GET
    Observable<ResponseBody> getProductAndPDesc(@Url String url);


    @GET
    Observable<ResponseBody> getCreateSalesOrder(@Url String url);

    @GET
    Observable<ResponseBody> getWRStatusReportCard(@Url String url);

    @GET
    Observable<ResponseBody> getSchemePointSlab(@Url String url);

    @GET
    Observable<ResponseBody> getMediaList(@Url String url);

    @GET
    Observable<ResponseBody> baseRetrofitInterface(@Url String url);

    @GET
    Observable<SearchResponseEntity> callSearchProductApi(@Url String url);


}