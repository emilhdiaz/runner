package com.emildiaz.runner.task;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetAddressTask extends AsyncTask<Location, Void, Address> {
    Context context;

    public GetAddressTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected Address doInBackground(Location... params) {
        Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
        Location location = params[0];
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch (IOException e) {
            return new Address(Locale.US);
        }
        return (addresses == null || addresses.size() <= 0) ?
            new Address(Locale.US) :
            addresses.get(0);
    }

    @Override
    protected void onPostExecute(Address address) {
        AddressUpdateListener listener = (AddressUpdateListener) this.context;
        listener.onAddressUpdated(address);
    }

    public interface AddressUpdateListener {
        public void onAddressUpdated(Address address);
    }
}
