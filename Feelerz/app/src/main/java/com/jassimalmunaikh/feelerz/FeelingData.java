package com.jassimalmunaikh.feelerz;

import org.json.JSONException;
import org.json.JSONObject;

class FeelingData
{
    String name;
    int size;
    String id;
    String colorCode;
    String emojie;
    String parentFeeling;

    FeelingData()
    {
        this.name = "";
        this.size = 0;
        this.colorCode = "";
    }


    void ParseData(JSONObject feelingData)
    {
        try{
            this.id = feelingData.getString("id");
            this.parentFeeling = feelingData.getString("parrent");
            this.name = feelingData.getString("name");
            this.size = feelingData.getInt("size");
            this.colorCode = feelingData.getString("color_code");
            this.emojie = feelingData.getString("emojie");
        }
        catch (JSONException e){}
    }


};
