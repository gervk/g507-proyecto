package g507.controldeconsumo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecomendFragment extends Fragment {

    private View view;
    private RecomendAdapter recomendAdapter;
    private ViewPager viewPager;

    public RecomendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recomend, container, false);

        recomendAdapter = new RecomendFragment.RecomendAdapter(getActivity().getSupportFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.pager_recomend);
        viewPager.setAdapter(recomendAdapter);

        return view;
    }

    public class RecomendAdapter extends FragmentStatePagerAdapter {

        public RecomendAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new RecomendLuzFragment();
                case 1:
                    return new RecomendAguaFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.title_frag_recomendLuz);
                case 1:
                    return getString(R.string.title_frag_recomendAgua);
                default:
                    return null;
            }
        }
    }



}
