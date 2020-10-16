package Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class Section_Pager_Adapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titlelist = new ArrayList<>();
    private FragmentManager fragmentManager;
    public Section_Pager_Adapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titlelist.get(position);
    }

    public void addfragment(Fragment fragment,String title){
        fragmentList.add(fragment);
        titlelist.add(title);
    }

    public void clear()
    {
        for(Fragment fragment : fragmentList)
            fragmentManager.beginTransaction().remove(fragment).commit();
        fragmentList.clear();
        titlelist.clear();
    }
}
