package in.innobins.customer;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sasuke on 8/9/15.
 */
public class AboutFragment extends Fragment {
    TextView link,googleplay,facebook,linkdin,version,termcond,Title;
    ImageView Togglebutton;
    public static String TAG = "AboutFragment";
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View Rootview = inflater.inflate(R.layout.aboutfragment, parent, false);

        Title = (TextView) Rootview.findViewById(R.id.title);
        Title.setText("about us");
        Togglebutton = (ImageView) Rootview.findViewById(R.id.togglebutton);
        Togglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(MainActivity.mDrawerPane);
            }
        });
        link = (TextView) Rootview.findViewById(R.id.websitelink);
        googleplay = (TextView) Rootview.findViewById(R.id.rategoogleplay);
        facebook = (TextView) Rootview.findViewById(R.id.likeonfacebook);
        linkdin = (TextView) Rootview.findViewById(R.id.followonlinkedin);
        version = (TextView) Rootview.findViewById(R.id.version);
        termcond = (TextView) Rootview.findViewById(R.id.ternandcond);

        return Rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        link.setText(Html.fromHtml("<a href='https://www.innobins.com'> innobins.com </a>"));
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent websiteintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.innobins.com"));
                startActivity(websiteintent);
            }
        });
        googleplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent playstoreintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=in.moovo.moovo"));
                    startActivity(playstoreintent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getActivity(), " You don't have any browser to open web page", Toast.LENGTH_LONG).show();
                }

            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Uri webpage = Uri.parse("https://www.facebook.com/innobins/");
                    Intent facebook = new Intent(Intent.ACTION_DEFAULT,webpage);
                    startActivity(facebook);

                }catch (ActivityNotFoundException e){

                    Toast.makeText(getActivity(), " You don't have any browser to open web page", Toast.LENGTH_LONG).show();
                }

            }
        });
        linkdin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent linkdinlink = new Intent(Intent.ACTION_VIEW,Uri.parse("https://linkedin.com"));
                    startActivity(linkdinlink);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getActivity(), " You don't have any browser to open web page", Toast.LENGTH_LONG).show();

                }

            }
        });
        termcond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent termcondlink = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.innobins.com/#"));
                    startActivity(termcondlink);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(getActivity(), " You don't have any browser to open web page", Toast.LENGTH_LONG).show();
                }
            }
        });
        version.setText("VERSION 3.0");

    }
}
