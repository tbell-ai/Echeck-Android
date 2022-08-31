package kr.co.tbell.echeck.views.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kr.co.tbell.echeck.views.fragment.usage.Usage1Fragment;
import kr.co.tbell.echeck.views.fragment.usage.Usage2Fragment;

public class FragmentTabAdapter extends FragmentStateAdapter {

    public FragmentTabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            default :
            case 0:
                return Usage1Fragment.newInstance();
            case 1:
                return Usage2Fragment.newInstance();

        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
