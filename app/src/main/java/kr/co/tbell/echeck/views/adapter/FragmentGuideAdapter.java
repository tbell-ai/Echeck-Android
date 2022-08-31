package kr.co.tbell.echeck.views.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kr.co.tbell.echeck.views.fragment.intro.Intro1Fragment;
import kr.co.tbell.echeck.views.fragment.intro.Intro2Fragment;
import kr.co.tbell.echeck.views.fragment.intro.Intro3Fragment;
import kr.co.tbell.echeck.views.fragment.intro.Intro4Fragment;

public class FragmentGuideAdapter extends FragmentStateAdapter {

    public FragmentGuideAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            default:
            case 0:
                return Intro1Fragment.newInstance();
            case 1:
                return Intro2Fragment.newInstance();
            case 2:
                return Intro3Fragment.newInstance();
            case 3:
                return Intro4Fragment.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
