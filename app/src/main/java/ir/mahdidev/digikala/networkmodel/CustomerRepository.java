package ir.mahdidev.digikala.networkmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ir.mahdidev.digikala.database.CustomerAddressModel;
import ir.mahdidev.digikala.database.RoomConfig;
import ir.mahdidev.digikala.networkmodel.address.WebServiceAddress;
import ir.mahdidev.digikala.networkutil.RetrofitApi;
import ir.mahdidev.digikala.networkutil.RetrofitConfig;
import ir.mahdidev.digikala.util.Const;
import ir.mahdidev.digikala.util.MyApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {
    private static CustomerRepository customerRepository;
    public static CustomerRepository getInstance(){
        if (customerRepository == null){
            customerRepository = new CustomerRepository();
        }
        return customerRepository;
    }

    private CustomerRepository(){
        roomConfig = MyApplication.getInstance().getRoomDb();
    }

    private RoomConfig roomConfig;
    private MutableLiveData<WebServiceAddress> customerAddressMutable = new MutableLiveData<>();

    public LiveData<List<CustomerAddressModel>> getAllCustomerAddress(int customerId){
        return roomConfig.customerAddressDao().getAllCustomerAddress(customerId);
    }
    public void inserCustomerAddress (CustomerAddressModel customerAddressModel){
        roomConfig.customerAddressDao().insert(customerAddressModel);
    }
    public void deleteCustomerAddress(CustomerAddressModel customerAddressModel){
        roomConfig.customerAddressDao().delete(customerAddressModel);
    }
    public void updateCustomerAddress(CustomerAddressModel customerAddressModel){
        roomConfig.customerAddressDao().update(customerAddressModel);
    }

    public void clearAddressData(){
        customerAddressMutable.setValue(new WebServiceAddress());
    }

    public MutableLiveData<WebServiceAddress> loadCustomerAddress(String latitude , String longitiude ){
        RetrofitConfig.getMapRetrofit().create(RetrofitApi.class).getCustomerAddress(latitude , longitiude
        , Const.RetrofitConst.GEOCODING_MAP_IR_API_KEY).enqueue(new Callback<WebServiceAddress>() {
            @Override
            public void onResponse(Call<WebServiceAddress> call, Response<WebServiceAddress> response) {
                if (response.isSuccessful()){
                    customerAddressMutable.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WebServiceAddress> call, Throwable t) {

            }
        });
        return customerAddressMutable;
    }

    public MutableLiveData<WebServiceAddress> getCustomerAddressMutable() {
        return customerAddressMutable;
    }
}