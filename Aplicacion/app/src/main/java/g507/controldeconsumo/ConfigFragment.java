package g507.controldeconsumo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConfigFragment extends Fragment {

    private View view;
    private ConfigAdapter configAdapter;
    private ViewPager viewPager;

    public ConfigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_config, container, false);

        configAdapter = new ConfigAdapter(getActivity().getSupportFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.pager_config);
        viewPager.setAdapter(configAdapter);

        return view;
    }

    public class ConfigAdapter extends FragmentStatePagerAdapter {

        public ConfigAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new ConfigCuentaFragment();
                case 1:
                    return new ConfigNotifFragment();
                case 2:
                    return new ConfigAguaFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.title_frag_config_cuenta);
                case 1:
                    return getString(R.string.title_frag_config_notif);
                case 2:
                    return getString(R.string.title_frag_config_serv_agua);
                default:
                    return null;
            }
        }
    }

}
