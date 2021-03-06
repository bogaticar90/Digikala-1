package ir.mahdidev.digikala.controller.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.SliderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.mahdidev.digikala.R;
import ir.mahdidev.digikala.adapter.CategoryRecyclerViewAdapter;
import ir.mahdidev.digikala.adapter.MainHorizontalRecyclerViewAdapter;
import ir.mahdidev.digikala.adapter.SliderEspecialProductAdapter;
import ir.mahdidev.digikala.controller.activity.CategoryListActivity;
import ir.mahdidev.digikala.controller.activity.ProductActivity;
import ir.mahdidev.digikala.controller.activity.ProductsListActivity;
import ir.mahdidev.digikala.eventbus.ListProductData;
import ir.mahdidev.digikala.eventbus.OnProductClickedMessage;
import ir.mahdidev.digikala.networkmodel.category.WebserviceCategoryModel;
import ir.mahdidev.digikala.networkmodel.product.WebserviceProductModel;
import ir.mahdidev.digikala.util.Const;
import ir.mahdidev.digikala.util.Pref;
import ir.mahdidev.digikala.viewmodel.MainFragmentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.category_recyclerView)
    RecyclerView categoryRecyclerView;
    @BindView(R.id.amazing_siggestion_recyclerView)
    RecyclerView amazingSuggestionRecyclerView;
    @BindView(R.id.newest_product_recyclerView)
    RecyclerView newestProductRecyclerView;
    @BindView(R.id.most_rating_product_recyclerView)
    RecyclerView ratingProductRecyclerView;
    @BindView(R.id.most_visited_product_recyclerView)
    RecyclerView visiting_productRecyclerView;
    @BindView(R.id.imageSlider)
    SliderView sliderView;
    @BindView(R.id.title_newest_product_horizontal)
    TextView titleNewestproduct;
    @BindView(R.id.title_rating_product_horizontal)
    TextView titleRatingProduct;
    @BindView(R.id.title_visiting_product_horizontal)
    TextView titleVisitingProduct;
    @BindView(R.id.newest_show_more)
    TextView newestShowMore;
    @BindView(R.id.rating_show_more)
    TextView ratingShowMore;
    @BindView(R.id.visiting_show_more)
    TextView visitingShowMore;

    private SliderEspecialProductAdapter sliderAdapter;
    private MainFragmentViewModel viewModel;

    private CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;
    private MainHorizontalRecyclerViewAdapter amazingSuggestionRecyclerViewAdapter;
    private MainHorizontalRecyclerViewAdapter newestProductRecyclerViewAdapter;
    private MainHorizontalRecyclerViewAdapter ratingProductRecyclerViewAdapter;
    private MainHorizontalRecyclerViewAdapter visitingProductRecyclerViewAdapter;

    private List<WebserviceCategoryModel> categoryList;

    private int visitingPage = 1;
    private int newestPage = 1;
    private int amazingSuggestionPage = 1;
    private int ratingPage = 1;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this , view);
        initViewModel();
    }

    private void initSliderView( List<WebserviceProductModel> productList) {
        sliderAdapter = new SliderEspecialProductAdapter(productList, getActivity());
        sliderView.setSliderAdapter(sliderAdapter);
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel.class);
        viewModel.getEspecialProducts().observe(this, this::initSliderView);
        viewModel.getCategoryListLiveData().observe(this , this::initCategoryRecyclerView);
        viewModel.getAmazingSuggestionListLiveData().observe(this , this::initamazingSuggestionRecyclerView);
        viewModel.getMostNewestListLiveData().observe(this , this::initNewestProductRecyclerView);
        viewModel.getMostRatingListLiveData().observe(this , this::initRatingProductRecyclerView);
        viewModel.getMostVisitingListLiveData().observe(this , this::initVisitingRecyclerView);
        if (categoryList ==null || categoryList.isEmpty()){
            viewModel.loadCategory();
        }

    }

    private void initVisitingRecyclerView(List<WebserviceProductModel> webserviceProductModels) {
        titleVisitingProduct.setText(getResources().getString(R.string.most_visiting));
        visitingShowMore.setOnClickListener(view -> {
            startActivity(ProductsListActivity.newIntent(getActivity(),new ListProductData("پربازدیدترین محصولات" ,
                    Const.OrderTag.MOST_VISITING_PRODUCT , "desc" , "")));
        });
        if (visitingProductRecyclerViewAdapter == null){
            visitingProductRecyclerViewAdapter = new MainHorizontalRecyclerViewAdapter(webserviceProductModels , getActivity());
            visiting_productRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, true));
            visiting_productRecyclerView.setAdapter(visitingProductRecyclerViewAdapter);
            visiting_productRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == webserviceProductModels.size() - 1) {
                        viewModel.loadMostVisitingListLiveData(++visitingPage);
                    }
                }
            });
        }
        else {

                visitingProductRecyclerViewAdapter.addProductList(webserviceProductModels);
                visitingProductRecyclerViewAdapter.notifyDataSetChanged();


        }
    }

    private void initRatingProductRecyclerView(List<WebserviceProductModel> webserviceProductModels) {
        titleRatingProduct.setText(getResources().getString(R.string.most_rating));
        ratingShowMore.setOnClickListener(view -> {
            startActivity(ProductsListActivity.newIntent(getActivity(),new ListProductData("پرامتیازترین محصولات" ,
                    Const.OrderTag.MOST_RATING_PRODUCT , "desc" , "")));
        });
        if (ratingProductRecyclerViewAdapter == null){
            ratingProductRecyclerViewAdapter = new MainHorizontalRecyclerViewAdapter(webserviceProductModels , getActivity());
            ratingProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, true));
            ratingProductRecyclerView.setAdapter(ratingProductRecyclerViewAdapter);
            ratingProductRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == webserviceProductModels.size() - 1) {
                        viewModel.loadMostRatingListLiveData(++ratingPage);
                    }
                }
            });
        }
        else {
                ratingProductRecyclerViewAdapter.addProductList(webserviceProductModels);
                ratingProductRecyclerViewAdapter.notifyDataSetChanged();

        }
    }

    private void initNewestProductRecyclerView(List<WebserviceProductModel> webserviceProductModels) {
            if (newestPage ==1 && !webserviceProductModels.isEmpty()){
                saveLastProductIdToPref(webserviceProductModels.get(0).getId());
            }
            titleNewestproduct.setText(getResources().getString(R.string.most_newest));
        newestShowMore.setOnClickListener(view ->
                startActivity(ProductsListActivity.newIntent(getActivity(),new ListProductData("جدیدترین محصولات" ,
                Const.OrderTag.MOST_NEWEST_PRODUCT , "desc" , ""))));
        if (newestProductRecyclerViewAdapter == null){
            newestProductRecyclerViewAdapter = new MainHorizontalRecyclerViewAdapter(webserviceProductModels , getActivity());
            newestProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, true));
            newestProductRecyclerView.setAdapter(newestProductRecyclerViewAdapter);
            newestProductRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == webserviceProductModels.size() - 1) {
                        viewModel.loadMostNewestListLiveData(++newestPage);
                    }
                }
            });
        }
        else {
                newestProductRecyclerViewAdapter.addProductList(webserviceProductModels);
                newestProductRecyclerViewAdapter.notifyDataSetChanged();
            }
    }

    private void saveLastProductIdToPref(int productId) {
        Pref.saveLastProductId(getActivity() , productId);
    }

    private void initamazingSuggestionRecyclerView(List<WebserviceProductModel> webserviceProductModels) {
        if (amazingSuggestionRecyclerViewAdapter==null){
            amazingSuggestionRecyclerViewAdapter = new MainHorizontalRecyclerViewAdapter(webserviceProductModels , getActivity());
            amazingSuggestionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, true));
            amazingSuggestionRecyclerView.setAdapter(amazingSuggestionRecyclerViewAdapter);
            amazingSuggestionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == webserviceProductModels.size() - 1) {
                        viewModel.loadAmazingSuggestionListLiveData(++amazingSuggestionPage);
                    }
                }
            });
        }
        else {

                amazingSuggestionRecyclerViewAdapter.addProductList(webserviceProductModels);
                amazingSuggestionRecyclerViewAdapter.notifyDataSetChanged();
            }


    }

    private void initCategoryRecyclerView(List<WebserviceCategoryModel> webserviceCategoryModels) {
        this.categoryList = webserviceCategoryModels;
        List<WebserviceCategoryModel> categoryModelList = new ArrayList<>();
        filterCategory(webserviceCategoryModels , categoryModelList);
        if (categoryRecyclerViewAdapter==null){
            categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(categoryModelList, getActivity() , Const.FROM_MAIN_FRAGMENT);
            categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
            categoryRecyclerView.setAdapter(categoryRecyclerViewAdapter);
        }
        categoryRecyclerViewAdapter.setCategoryRecyclerViewAdapterInterface((position ,webserviceCategoryModel) -> {
            startActivity(CategoryListActivity.newIntent(getActivity() ,position));
        });
    }


    @Subscribe
    public void onProductClicked(OnProductClickedMessage message){
        viewModel.setProductId(message.getProductId());
        viewModel.loadSingleProduct(message.getProductId());
        startActivity(ProductActivity.newIntent(getActivity()));
    }

    private void filterCategory(List<WebserviceCategoryModel> categoryModelList, List<WebserviceCategoryModel> categoryList) {
        for (WebserviceCategoryModel categoryModel : categoryModelList){
            if (categoryModel.getParent()==0){
                categoryList.add(categoryModel);
            }
        }
    }
}
