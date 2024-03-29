package com.mobileapps.umbrella.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileapps.umbrella.R;
import com.mobileapps.umbrella.models.currentWeather.CurrentWeather;
import com.mobileapps.umbrella.utilities.SharedPreferences;
import com.mobileapps.umbrella.utilities.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainViewInterface
{

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.tvWeatherDegree)
    TextView tvDegree;

    @BindView(R.id.tvWeekDay)
    TextView tvWeekDay;

    @BindView(R.id.tvCelcious)
    TextView tvCelcius;

    @BindView(R.id.tvFarenheit)
    TextView tvFarenheit;


    @BindView(R.id.tvWeatherDescription)
    TextView tvWeatherDescription;

    @BindView(R.id.etZipCode)
    EditText etZipCode;

    @BindView(R.id.tvHumaditi)
    TextView tvHumaditi;

    @BindView(R.id.tvWindSpeed)
    TextView tvWindSpeed;

    @BindView(R.id.imgWeather)
    ImageView imgWeather;

    @BindView(R.id.clCurrentWeather)
    ConstraintLayout clCurrentWeather;

    @BindView(R.id.tvSearch)
    TextView tvSearch;


    private String TAG = "MainActivity";
    MainPresenter mainPresenter;
    Utils  utils;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils= new Utils(this);
        sharedPreferences = new SharedPreferences();

        ButterKnife.bind(this);

        etZipCode.setText(sharedPreferences.getZipCode(this));

        if(!etZipCode.getText().toString().equals(""))
        {
            validateZipCode();
            tvWeekDay.setText(utils.getCurrentDayWithDate());
        }


    }

    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tvSearch:
                tvSearch.setVisibility(View.INVISIBLE);
                validateZipCode();
                break;

            case R.id.tvCelcious:
                sharedPreferences.setFarenheit(this,false);
                tvCelcius.setTextColor(getColor(R.color.colorSelect));
                tvFarenheit.setTextColor(getColor(R.color.colorNoSelect));
                tvDegree.setText(utils.getDegree());
                break;

            case R.id.tvFarenheit:
                sharedPreferences.setFarenheit(this,true);
                tvCelcius.setTextColor(getColor(R.color.colorNoSelect));
                tvFarenheit.setTextColor(getColor(R.color.colorSelect));
                tvDegree.setText(utils.getDegree());
                break;

            case R.id.tvGoToWeatherForDays:
                Intent intent = new Intent(this,DetailsOfTheWeekActivity.class);
                startActivity(intent);



        }
    }

    public void validateZipCode()
    {
        if(!etZipCode.getText().toString().equals(""))
        {
            setupMVP();
            setupMVP();
            getWeather();
            sharedPreferences.setZipCode(this,etZipCode.getText().toString());
            tvWeekDay.setText(utils.getCurrentDayWithDate());
            //Todo keep the information for search
            setWeatherWithRightDegree();

        }
        else
        {
            Toast.makeText(this, "Please enter the Zip Code", Toast.LENGTH_SHORT).show();
            tvSearch.setVisibility(View.VISIBLE);
        }
    }

    private void setWeatherWithRightDegree()
    {
        if(sharedPreferences.isFarenheit(this))
        {
            tvCelcius.setTextColor(getColor(R.color.colorNoSelect));
            tvFarenheit.setTextColor(getColor(R.color.colorSelect));
        }
        else
        {
            tvCelcius.setTextColor(getColor(R.color.colorSelect));
            tvFarenheit.setTextColor(getColor(R.color.colorNoSelect));
        }
    }

    private void setupMVP() {
        mainPresenter = new MainPresenter(this,etZipCode.getText().toString());
    }

    private void getWeather() {
        mainPresenter.getCurrentWeather();
    }

    @Override
    public void displayWeather(CurrentWeather currentWeather) {
        if(currentWeather!=null) {
            String title = currentWeather.getName() + ","+etZipCode.getText().toString();
            tvTitle.setText(title);


            sharedPreferences.setDegree(this,currentWeather.getMain().getTemp());
            tvDegree.setText(utils.getDegree());
            tvHumaditi.setText(utils.getHumedity(currentWeather.getMain().getHumidity()));
            tvWindSpeed.setText(utils.getWindSpeed(currentWeather.getWind().getSpeed()));
            tvWeatherDescription.setText(currentWeather.getWeather().get(0).getDescription());
            Picasso.with(this)
                    .load(utils.getImageUrl(currentWeather.getWeather().get(0).getIcon()))
                    .into(imgWeather);
            clCurrentWeather.setVisibility(View.VISIBLE);
        }else{
            Log.d(TAG,"Movies response null");
        }
        tvSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayError(String s)
    {
        Toast.makeText(this, "Try to insert the zip code again", Toast.LENGTH_SHORT).show();
        //todo add clean the edittext
        tvSearch.setVisibility(View.VISIBLE);
    }
}
