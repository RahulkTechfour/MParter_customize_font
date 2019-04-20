package com.luminous.mpartner.product_service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.luminous.mpartner.MyApplication;
import com.luminous.mpartner.R;
import com.luminous.mpartner.databinding.FragmentProductServiceBinding;
import com.luminous.mpartner.events.ProductCatalogClickEvent;
import com.luminous.mpartner.fragments.BaseFragment;
import com.luminous.mpartner.network.RetrofitClient;
import com.luminous.mpartner.network.RetrofitInterface;
import com.luminous.mpartner.network.ServerConfig;
import com.luminous.mpartner.network.entities.CatalogMenuFooterData;
import com.luminous.mpartner.network.entities.CatalogProductsEntity;
import com.luminous.mpartner.network.entities.CatalogUpperMenuEntity;
import com.luminous.mpartner.network.entities.ProductCatalog;
import com.luminous.mpartner.network.entities.ProductCatalogDetail;
import com.luminous.mpartner.network.entities.ProductCatalogUpper;
import com.luminous.mpartner.network.entities.ProductCategoryEntity;
import com.luminous.mpartner.utilities.CommonUtility;
import com.luminous.mpartner.utilities.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProductServiceFragment extends BaseFragment implements OnRecyclerViewItemClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentProductServiceBinding binding;
    private Context context;
    private RetrofitInterface apiInterface;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private CatalogMenuFooterData catalogMenuFooterData;
    private CatalogUpperMenuEntity catalogMenuHeaderData;
    private CatalogProductsEntity catalogProductsData;
    private CatalogMenuFooterAdapter catalogMenuFooterAdapter;
    private CatalogMenuHeaderAdapter catalogMenuHeaderAdapter;
    private CatalogProductPopupAdapter catalogProductPopupAdapter;
    private CatalogProductRecyclerViewAdapter productRecyclerViewAdapter;

    private List<ProductCatalogDetail> productCatalogDetailList;

    private PopupWindow popupWindow;
    private HashMap<Integer, ProductCatalogDetail> productCatalogDetailHashMap;
    private String clickedProductCatalogName = "";
    private int clickedProductCatalogId = -1;
    private int clickedFooterPosition = 0;
    private List<ProductCatalog> filteredProductCatalogList;
    private static final int STORAGE_PERMISSION_CODE_3 = 1000;

    public ProductServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchemeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductServiceFragment newInstance(int param1, String param2) {
        ProductServiceFragment fragment = new ProductServiceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clickedProductCatalogId = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_service, container, false);
        View view = binding.getRoot();
        productCatalogDetailHashMap = new HashMap<Integer, ProductCatalogDetail>();
        apiInterface = RetrofitClient.getInstance().create(RetrofitInterface.class);

        initViews();

        MyApplication.getApplication()
                .bus()
                .toObservable()
                .subscribe(object -> {
                    if (object instanceof ProductCatalogClickEvent) {
                        ProductCatalogClickEvent productCatalogClickEvent = (ProductCatalogClickEvent) object;
                        if (catalogProductsData != null && catalogProductsData.getProductCatalog() != null) {
                            clickedProductCatalogId = productCatalogClickEvent.getProductCatalogId();
                            filterProductDetails();
                        }
                    }
                });

        if (CommonUtility.isNetworkAvailable(context)) {
            getCatalogFooterData();
        }

        binding.pdfDownloadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPdf();
            }
        });
        return view;
    }

    public void downloadPdf() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE_3);
        } else {

            if (!CommonUtility.isNetworkAvailable(getContext())) {
                Toast.makeText(getContext(), "No internet", Toast.LENGTH_SHORT).show();
                return;
            }
            if (catalogMenuFooterData != null)
                CommonUtility.downloadFile(getContext(), catalogMenuFooterData.getProductCategory().get(clickedFooterPosition).getUrlProductCategoryPdf(), catalogMenuFooterData.getProductCategory().get(clickedFooterPosition).getProductCatagoryName() + " Catalog.pdf");
            else
                Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();


        }
    }

    public void filterProductDetails() {

        filteredProductCatalogList = new ArrayList<>();

        for (ProductCatalog productCatalog : catalogProductsData.getProductCatalog()) {
            if (productCatalog.getId() == clickedProductCatalogId) {
                filteredProductCatalogList.add(productCatalog);
            }
        }
        setCategoryProductRecyclerView(true);

//        int scrolledPosition = -1;
//        for (int i = 0; i < catalogProductsData.getProductCatalog().size(); i++) {
//            ProductCatalog productCatalog = catalogProductsData.getProductCatalog().get(i);
//            if (productCatalog.getId() == clickedProductCatalogId) {
//                scrolledPosition = i;
//                productRecyclerViewAdapter.setClickablePosition(scrolledPosition);
//                productRecyclerViewAdapter.notifyDataSetChanged();
//                binding.productRecyclerView.smoothScrollToPosition(scrolledPosition);
//                break;
//            }
//        }
    }


    private void initViews() {

        binding.footerRecycleView.setItemAnimator(new DefaultItemAnimator());
        if (Build.VERSION.SDK_INT >= 21)
            binding.footerRecycleView.setNestedScrollingEnabled(true);
        binding.headerRecycleView.setItemAnimator(new DefaultItemAnimator());
        if (Build.VERSION.SDK_INT >= 21)
            binding.headerRecycleView.setNestedScrollingEnabled(true);

        binding.flFooterArrow.setOnClickListener(this);
        binding.flHeaderArrow.setOnClickListener(this);
        binding.toggleButton.setChecked(true);
        binding.toggleButton.setOnCheckedChangeListener(this);


    }

    private void setProductServicePageUI() {

        if (catalogMenuFooterData != null) {
            setFooterAdapter(catalogMenuFooterData.getProductCategory());
        }

        if (catalogMenuHeaderData != null) {
            setHeaderAdapter(catalogMenuHeaderData.getProductCatalogUpper());
        }

        if (filteredProductCatalogList != null && filteredProductCatalogList.size() > 0) {
            //TODO popup menu item
//            showProductCatalogPopup();
            setCategoryProductRecyclerView(false);
            binding.container.setVisibility(View.VISIBLE);
        }

        if (clickedProductCatalogId != -1) {
            filterProductDetails();
        }
    }

    private void setCategoryProductRecyclerView(boolean isFiltered) {
        productRecyclerViewAdapter = new CatalogProductRecyclerViewAdapter(getContext(), filteredProductCatalogList, this);
        productRecyclerViewAdapter.setIsFiltered(isFiltered);
        binding.productRecyclerView.setAdapter(productRecyclerViewAdapter);
    }

    private void setFooterAdapter(List<ProductCategoryEntity> productCategory) {
        catalogMenuFooterAdapter = new CatalogMenuFooterAdapter(getContext(), productCategory, this);
        if (clickedProductCatalogId == -1) {
            catalogMenuFooterAdapter.setClickablePosition(clickedFooterPosition);
        } else {
            catalogMenuFooterAdapter.setClickablePosition(-1);
        }
        binding.footerRecycleView.setAdapter(catalogMenuFooterAdapter);
    }

    private void setHeaderAdapter(List<ProductCatalogUpper> productCategory) {
        catalogMenuHeaderAdapter = new CatalogMenuHeaderAdapter(getContext(), productCategory, filteredProductCatalogList, this);
        binding.headerRecycleView.setAdapter(catalogMenuHeaderAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductServiceFragment.OnFragmentInteractionListener) {
            mListener = (ProductServiceFragment.OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }

        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    public void onItemCLicked(View view, int position) {

        if (view == null && position == -1) {
            clickedProductCatalogName = "";
        }
        if (view instanceof TextView) {
            clickedProductCatalogId = -1;

            catalogMenuHeaderAdapter.setClickablePosition(position);
            clickedProductCatalogName = catalogMenuHeaderData.getProductCatalogUpper().get(position).getCatalogMenuUpperName();

        } else if (view instanceof ImageView) {
            productRecyclerViewAdapter.setClickablePosition(position);
            productRecyclerViewAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onItemCLicked(View view, int position, String source) {
        if (source == "footer") {
            if (catalogMenuFooterData.getProductCategory() != null && catalogMenuFooterData.getProductCategory().size() > 0 && catalogMenuFooterData.getProductCategory().get(0) != null) {
                getCatalogHeaderAndProductData(catalogMenuFooterData.getProductCategory().get(position).getId());

                clickedFooterPosition = position;
                catalogMenuFooterAdapter.setClickablePosition(position);
                catalogMenuFooterAdapter.notifyDataSetChanged();

                clickedProductCatalogId = -1;
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fl_header_arrow:
                binding.headerRecycleView.scrollBy(binding.headerRecycleView.computeHorizontalScrollExtent() - 100, 0);
                break;
            case R.id.fl_footer_arrow:
                binding.footerRecycleView.scrollBy(binding.footerRecycleView.computeHorizontalScrollExtent() - 100, 0);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            binding.inverlastTv.setTextColor(getResources().getColor(R.color.text_blue_color));
            binding.electraTv.setTextColor(getResources().getColor(R.color.text_color_entry));
        } else {
            binding.inverlastTv.setTextColor(getResources().getColor(R.color.text_color_entry));
            binding.electraTv.setTextColor(getResources().getColor(R.color.text_blue_color));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    private void getCatalogProductsDetails(int productCatalogId) {
//        String url = ServerConfig.newUrl(String.format(ServerConfig.getCatalogProductsDetailsUrl(), productCatalogId), context, ProductServiceFragment.class.getSimpleName() + ".class");
//        showProgressDialog();
//
//        apiInterface.getCatalogProductsDetails(url)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ProductCatalogDetailEntity>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        compositeDisposable.add(d);
//                    }
//
//                    @Override
//                    public void onNext(ProductCatalogDetailEntity productCatalogDetailEntity) {
//                        dismissProgressDialog();
//                        if (productCatalogDetailEntity.getStatus().equalsIgnoreCase("200")) {
//                            productCatalogDetailList = productCatalogDetailEntity.getProductCatalogDetails();
//                            productCatalogDetailHashMap.put(productCatalogId, productCatalogDetailList.get(0));
//                            productRecyclerViewAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        dismissProgressDialog();
//                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
//                    }
//                });
//
//    }

    private void getCatalogFooterData() {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        showProgressDialog();
        String url = ServerConfig.newUrl(ServerConfig.getCatalogMenuFooterUrl(), context, ProductServiceFragment.class.getSimpleName() + ".class");

        apiInterface.getCatalogMenuFooterData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CatalogMenuFooterData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(CatalogMenuFooterData catalogMenuFooterDataResponse) {
                        dismissProgressDialog();
                        if (catalogMenuFooterDataResponse.getStatus().equalsIgnoreCase("200")) {
                            catalogMenuFooterData = catalogMenuFooterDataResponse;
                            if (catalogMenuFooterData != null) {
                                if (catalogMenuFooterData.getProductCategory() != null && catalogMenuFooterData.getProductCategory().size() > 0 && catalogMenuFooterData.getProductCategory().get(0) != null) {
                                    getCatalogHeaderAndProductData(catalogMenuFooterData.getProductCategory().get(0).getId());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }

    private void getCatalogHeaderAndProductData(long productCategoryId) {

        if (!CommonUtility.isNetworkAvailable(context)) {
            return;
        }

        showProgressDialog();

        getHeaderAndUpperMenuData(productCategoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<CatalogUpperMenuEntity, CatalogProductsEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Pair<CatalogUpperMenuEntity, CatalogProductsEntity> catalogMenuFooterDataCatalogUpperMenuEntityPair) {
                        dismissProgressDialog();

                        CatalogUpperMenuEntity catalogUpperMenuEntity = catalogMenuFooterDataCatalogUpperMenuEntityPair.first;
                        CatalogProductsEntity catalogProductsEntity = catalogMenuFooterDataCatalogUpperMenuEntityPair.second;

                        if (catalogUpperMenuEntity != null && catalogUpperMenuEntity.getStatus().equalsIgnoreCase("200")) {
                            catalogMenuHeaderData = catalogUpperMenuEntity;
                        }

                        if (catalogProductsEntity != null && catalogProductsEntity.getStatus().equalsIgnoreCase("200")) {
                            catalogProductsData = catalogProductsEntity;
                            filteredProductCatalogList = catalogProductsEntity.getProductCatalog();
                        }

                        setProductServicePageUI();
                    }


                    @Override
                    public void onError(Throwable e) {
                        dismissProgressDialog();
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        CommonUtility.printLog("CatalogMenuFooterData", "onComplete");
                    }
                });

    }

    public Observable<Pair<CatalogUpperMenuEntity, CatalogProductsEntity>> getHeaderAndUpperMenuData(long productCategoryId) {

        String url1 = ServerConfig.newUrl(String.format(ServerConfig.getCatalogMenuHeaderUrl(), productCategoryId), context, ProductServiceFragment.class.getSimpleName() + ".class");
        String url2 = ServerConfig.newUrl(String.format(ServerConfig.getCatalogProductsUrl(), productCategoryId), context, ProductServiceFragment.class.getSimpleName() + ".class");

        Observable<CatalogUpperMenuEntity> catalogUpperMenuEntityObservable = apiInterface.getCatalogMenuUpperMenuData(url1);
        Observable<CatalogProductsEntity> catalogProductsEntityObservable = apiInterface.getCatalogProducts(url2);

        return catalogUpperMenuEntityObservable.zipWith(catalogProductsEntityObservable, Pair::new);
    }

    private void showProductCatalogPopup() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.catalog_products_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = 800;
        boolean focusable = false; // lets taps outside the popup also dismiss it if true
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);


        // show the popup window
        popupWindow.showAsDropDown(binding.headerRecycleView);

        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        catalogProductPopupAdapter = new CatalogProductPopupAdapter(context, filteredProductCatalogList, popupWindow);
        recyclerView.setAdapter(catalogProductPopupAdapter);

    }
}
