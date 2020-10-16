package com.jassimalmunaikh.feelerz;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

interface ServerCallObserver
{
    void OnSuccess(JSONObject response,String apiName);
    void OnFailure(String errorMessage,String apiName);
    void OnNetworkError();
}

public class  RequestHandler {

    Context mContext = null;
    ServerCallObserver mObserver;

    private RequestHandler(){}

    RequestHandler(Context context,ServerCallObserver observer)
    {
        this.mContext = context;
        this.mObserver = observer;
    }


    protected void MakeRequest(Map<String, String> dataBlob, final String apiName)
    {
        Set<Map.Entry<String,String>> set = dataBlob.entrySet();
        JSONObject request = new JSONObject();
        try {
            for(Map.Entry<String,String> pair : set) {
                request.put(pair.getKey(),pair.getValue());
            }
        } catch (JSONException e) {
            Log.e("API CALL ERROR",e.getMessage());
        }

        String dataURL = mContext.getResources().getString(R.string.Link) + apiName;

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest
                (Request.Method.POST, dataURL, request, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getInt("status") == 1) {
                                mObserver.OnSuccess(response,apiName);
                            }
                            else if(response.getInt("status") == 0)
                            {
                                mObserver.OnFailure(response.getString("message"),apiName);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {
                       if(error instanceof NetworkError)
                           mObserver.OnNetworkError();
                    }
                });

        jsArrayRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
        );
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(mContext).addToRequestQueue(jsArrayRequest);
    }



}
