package com.jassimalmunaikh.feelerz;

import java.util.ArrayList;

interface TopLevelFrag
{
    public Boolean canBeRemovedWithBackButton = true;
    public void OnManualClose();
    public void Close();
}


public class GlobalFragmentStack {

    private static final GlobalFragmentStack ourInstance = new GlobalFragmentStack();

    ArrayList<TopLevelFrag> currentActiveFrags = new ArrayList<TopLevelFrag>();

    public static GlobalFragmentStack getInstance() {
        return ourInstance;
    }

    private GlobalFragmentStack() {
    }

    public void onBackButton()
    {
        int size = this.currentActiveFrags.size();
        if(size > 0) {
            TopLevelFrag frag = this.currentActiveFrags.get(size - 1);
            if(frag.canBeRemovedWithBackButton) {
                frag.Close();
                this.currentActiveFrags.remove(frag);
            }
        }
    }

    public void Flush()
    {
        for(int i = 0; i < this.currentActiveFrags.size() ; i++)
            this.currentActiveFrags.get(i).Close();
        this.currentActiveFrags.clear();
    }

    public void clear(){
        this.currentActiveFrags.clear();
    }

    public void Register(TopLevelFrag frag)
    {
        this.currentActiveFrags.add(frag);
    }

    public void Unregister(TopLevelFrag frag)
    {
        if(this.currentActiveFrags.contains(frag))
            this.currentActiveFrags.remove(frag);
    }
}
