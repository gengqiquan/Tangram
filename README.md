# Tangram
# Custom software disk, automatically pop-up hidden
## example 
![Email](https://github.com/gengqiquan/Tangram/blob/master/images/device-2017-06-09-101954.png)![NUMBER_DECIMAL](https://github.com/gengqiquan/Tangram/blob/master/images/number_decmail.png )![NUMBER](https://github.com/gengqiquan/Tangram/blob/master/images/phone.png )![VIN_CODE](https://github.com/gengqiquan/Tangram/blob/master/images/vin_code.png)
### already provided type
    NUMBER,
    NUMBER_DECIMAL,
    PHONE,
    Email,
    VIN_CODE;
    
### how to use
```
  <com.gengqiquan.library.TangramEditText
            android:id="@+id/number_decimal"
            android:layout_width="match_parent"
            android:layout_height="48dp"
            android:layout_marginTop="30dp"
            android:background="#f5f5f5"
            android:password="true"
            android:textSize="18sp"
            app:keyboard_type="NUMBER_DECIMAL" />
```
```
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Tangram.track(this)
    }
```
 ## Maven build settings 
 ### maven
 ```
 <dependency>
  <groupId>com.gengqiquan</groupId>
  <artifactId>tangram</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
 ```
 ### gradle
 ```
 compile 'com.gengqiquan:tangram:0.0.1'
 ```
 ### lvy
 ```
 <dependency org='com.gengqiquan' name='tangram' rev='0.0.1'>
  <artifact name='tangram' ext='pom' ></artifact>
</dependency>
 ```
 thanks
