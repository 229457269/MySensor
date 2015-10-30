package zhaoq_hero.mysensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
implements SensorEventListener{


    private TextView txt;
    private SensorManager manager;
    private Sensor Lightsensor;


    private Sensor accelerometerSensor;//加速度传感器

    private TextView acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView) findViewById(R.id.txt_sensor);

        acc = (TextView) findViewById(R.id.accele);

        points = new Vector<>();

        //列举手机中的所有的传感器：

        //1，所有的传感器   都通过SensorManager来获取和管理：
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /**
         *
         Sensor.TYPE_ORIENTATION：方向传感器。
         Sensor.TYPE_ACCELEROMETER：重力传感器。加速度传感器
         Sensor.TYPE_LIGHT：光线传感器。
         Sensor.TYPE_MAGNETIC_FIELD：磁场传感器。
         */
        //2,获取所有的传感器：
        List<Sensor> sensorList = manager.getSensorList(Sensor.TYPE_ALL);


        StringBuilder sb = new StringBuilder("传感器:\n");

        //Sensor 主要描述传感器的参数信息，无法设置。
        for (Sensor sensor:sensorList) {

            sb.append(sensor.getName()+"  ").append(sensor.getVendor())
                    .append(sensor.getResolution())
                    .append("  ").append(sensor.getType()+"\n");

            Log.i("info", sensor.toString());
        }

        String str = sb.toString();

        txt.setText(str);

        //-------------------

        //获取亮度传感器的使用：
        accelerometerSensor = manager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (Lightsensor != null) {

            //注册传感器监听接口   用于接受传感器捕获的数据

            //注册监听器  参数1：用于接受数据的接口
            //参数二代表  要监听哪一个传感器：
            //参数三 ：代表  传感器采样频率
            manager.registerListener(this, Lightsensor,
                    SensorManager.SENSOR_DELAY_NORMAL//采样频率****
            );
        }


        //加速度的传感器：------------------
        if (Build.VERSION.SDK_INT >=21){
            accelerometerSensor = manager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER,true);
        }else{
            accelerometerSensor = manager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);
        }

        if (accelerometerSensor != null) {

            manager.registerListener(
                    this,
                    accelerometerSensor,//注册监听器
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //因为  registerListener制定了传感器与接口的对应关系
        //同时 一个接口  可能会包含多种传感器的支持，
        //取消注册接口的这种方式相当于取消所有的传感器
        manager.unregisterListener(this);
    }




    /**
     * 传感器数据发生变化
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //处理  Sersor
        int type = event.sensor.getType();

        switch (type){

            case Sensor.TYPE_LIGHT:

                adjustScreenBright(event);//调整亮度

                break;

            case Sensor.TYPE_ACCELEROMETER://处理加速度  摇一摇

                processShake(event);

                break;

        }
    }


    private Sensor acclerometerSeneor;
    private Vector<float[]>  points;

    //处理摇一摇数据
    private void processShake(SensorEvent event){

        float[] values = event.values;//加速度传感器的数值 三维数组 有三个  x,y,z,三个方向的变化

        float x = values[0];
        float y= values[1];
        float z = values[2];

        int size = points.size();
        if (size <30) {

            points.add(values);

        }else {
            //TODO：遍历每一个点  计算每一个方向>9.8  以上的点的个数超过多少
            //认为是摇一摇




        }


        Log.i("info","重力传感器x:"+x+"y:"+y+"z:"+z);
        acc.setText("重力传感器x:"+x+"y:"+y+"z:"+z);

    }




    //调整亮度
    private void adjustScreenBright(SensorEvent event) {
        float[] values = event.values;

        //光线的强度
        float light = values[0];

        //亮度传感器  数值的常量定义
        float lightSunlightMax = SensorManager.LIGHT_SUNLIGHT_MAX;

        txt.setText("当前亮度Light:"+light+"\n亮度最大值："+lightSunlightMax);


        //亮度传感器的  常量定义：
        Window window = getWindow();

        WindowManager.LayoutParams attributes = window.getAttributes();

        float currlight = 1;

        //有光情况下
        if (light >= 0  //没有光亮
                &&light <SensorManager.LIGHT_FULLMOON) { //月光强度

            currlight = 0.1f;//最亮

        }else if(light >=SensorManager.LIGHT_FULLMOON
                && light<SensorManager.LIGHT_CLOUDY){  //多云

            currlight = 0.2f;

        }else if(light >=SensorManager.LIGHT_CLOUDY
                && light<SensorManager.LIGHT_SUNRISE) {  //早上

            currlight = 0.3f;

        }else if(light >=SensorManager.LIGHT_SUNRISE
                && light<SensorManager.LIGHT_OVERCAST) { //正午

            currlight = 0.4f;

        }
        else if(light >=SensorManager.LIGHT_OVERCAST
                && light<SensorManager.LIGHT_SHADE) { //正午

            currlight = 0.5f;

        }
        else if(light >=SensorManager.LIGHT_SHADE
                && light<SensorManager.LIGHT_SUNLIGHT) { //正午

            currlight = 0.7f;

        }
        else if(light >=SensorManager.LIGHT_SUNLIGHT
                && light<SensorManager.LIGHT_SUNLIGHT_MAX) { //正午

            currlight = 1f;

        }

        attributes.screenBrightness = currlight;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
