package learning.customnativeads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoadItemsService {

    /**
     * Adds {@link ScrollingItem}'s from a JSON file.
     */
    public static ArrayList<Object> addMenuItemsFromJson(Context context) {

        ArrayList<Object> items = new ArrayList<>();
        try {
            String jsonDataString = readJsonDataFromFile(context);
            JSONArray menuItemsJsonArray = new JSONArray(jsonDataString);

            for (int i = 0; i < menuItemsJsonArray.length(); ++i) {

                JSONObject menuItemObject = menuItemsJsonArray.getJSONObject(i);

                String menuItemName = menuItemObject.getString("name");
                String menuItemDescription = menuItemObject.getString("description");
                String menuItemPrice = menuItemObject.getString("price");
                String menuItemCategory = menuItemObject.getString("category");
                String menuItemImageName = menuItemObject.getString("photo");

                ScrollingItem menuItem = new ScrollingItem(menuItemName, menuItemDescription, menuItemPrice,
                        menuItemCategory, menuItemImageName);
                items.add(menuItem);
            }
        } catch (IOException | JSONException exception) {
            Log.e(MainActivity.class.getName(), "Unable to parse JSON file.", exception);
        }
        return items;
    }

    /**
     * Reads the JSON file and converts the JSON data to a {@link String}.
     *
     * @return A {@link String} representation of the JSON data.
     * @throws IOException if unable to read the JSON file.
     */
    private static String readJsonDataFromFile(Context context) throws IOException {

        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();

        try {
            String jsonDataString = null;
            inputStream = context.getResources().openRawResource(R.raw.menu_items_json);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"));
            while ((jsonDataString = bufferedReader.readLine()) != null) {
                builder.append(jsonDataString);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return new String(builder);
    }

    public static String getListAdUnitId(Context context) {
        return context.getResources().getString(R.string.pan_staggered_ad_unit_id);
    }

    public static AdRequest.Builder getAdRequestBuilder(boolean shouldShowTestAds){
        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        if (shouldShowTestAds) {
            requestBuilder
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                    .addTestDevice("FD753978A2E3416E87E7AAEFD43C226E");
        }
        return requestBuilder;
    }
}
