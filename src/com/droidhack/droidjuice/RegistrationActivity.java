package com.droidhack.droidjuice;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends Activity{

	EditText userNameText, userMobileNumberText;
	Button registerButton;
	ProgressDialog loadingDialog;
	SharedPreferences appSettings;
    SharedPreferences.Editor prefEditor;
    String server_user_name="";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appSettings = getSharedPreferences("DroidJuiceAppPref", MODE_PRIVATE);
        prefEditor = appSettings.edit();
        
        HelperMethods.log("Username: "+appSettings.getString("user_name", ""));
        
        if(appSettings.getString("user_name", "").equals(""))
        {
            setContentView(R.layout.activity_registration);
            
            userNameText=(EditText)findViewById(R.id.userNameText);
            userMobileNumberText=(EditText)findViewById(R.id.userMobileText);
            
            registerButton=(Button)findViewById(R.id.registerButton);
            registerButton.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				String user_name=userNameText.getEditableText().toString().trim();
    				String mobile_number=userMobileNumberText.getEditableText().toString().trim();
    				
    				if(user_name.equals("")&&mobile_number.equals(""))
    				{
    					Toast.makeText(getApplicationContext(), "Please specify both the details", Toast.LENGTH_SHORT).show();
    				}
    				else
    				{
    					JSONObject serverData=new JSONObject();
    					try {
    						serverData.put("user_twitter", user_name);
    						serverData.put("user_mobile_no", mobile_number);
    						serverData.put("device_manufacturer", Build.MANUFACTURER);
    						serverData.put("device_model", Build.MODEL);
    						serverData.put("device_radio", Build.RADIO);
    						
    					} catch (JSONException e) {
    						e.printStackTrace();
    					}
    					
    					HelperMethods.log(serverData.toString());
    					
    					SendDataToServer sendData=new SendDataToServer();
    					sendData.execute(new String[] {serverData.toString()});
    					
    				}
    			}
    		});
        }
        else
        {
        	HelperMethods.log("Trying to launch Intro Activity");
        	Intent settingsPageIntent=new Intent(getApplicationContext(), SettingsActivity.class);
        	startActivity(settingsPageIntent);
        	
        	finish();
        }
         
        
	}
	
	
	
	private class SendDataToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            loadingDialog = ProgressDialog.show(RegistrationActivity.this, "", "Sending info ...", true, false, null);

        }

        @Override
        protected String doInBackground(String... arg0) {
            
        	
        	 String requestURL = "http://droidjuice.me/api.php?q=create_user&data="+URLEncoder.encode(arg0[0]);
             
        	 HelperMethods.log("Final URL: "+requestURL);
             HttpClient httpclient = new DefaultHttpClient();
             HttpResponse response;

             try {
                 response = httpclient.execute(new HttpGet(requestURL));
                 StatusLine statusLine = response.getStatusLine();
                 if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                     ByteArrayOutputStream out = new ByteArrayOutputStream();
                     response.getEntity().writeTo(out);
                     out.close();
                     String responseString = out.toString().trim();
                     
                     HelperMethods.log("RESPONSE: " + responseString);
                     
                     JSONObject responseJSON=null;
 					 String responseStatus=null;
 					 //String responseUserID=null;
                     
						try {
							responseJSON = new JSONObject(responseString);
							responseStatus=responseJSON.getString("response");
							//responseUserID=responseJSON.getString("user_id");
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						 
					
						return responseStatus;
                    
                   

                 } else {
                     response.getEntity().getContent().close();

                     HelperMethods.log("Data : Exception1");
                 }
             } catch (ClientProtocolException e) {
            	 HelperMethods.log("Data : Exception2 "+e.getMessage());
             } catch (IOException e) {
            	 HelperMethods.log("Data : Exception3 "+e.getMessage());
             }  
        	
        	
            return null;
        }

        @Override
        protected void onPostExecute(String responseStatus) {
            super.onPostExecute(responseStatus);
             loadingDialog.dismiss();
           
             if(responseStatus.equals("-1"))
             {
 	           // HelperMethods.log("Twitter ID Taken");
  	            Toast.makeText(getApplicationContext(), "Oops, this Droid Juice handle is already taken :(", Toast.LENGTH_LONG).show();
             }
             else if (responseStatus.equals("0"))
             {
   	            Toast.makeText(getApplicationContext(), "Oops, something went wrong :( \n Check your network connection!", Toast.LENGTH_LONG).show();
             }
             else 
             {
 	            //HelperMethods.log("Data sent: Success");
 	            Toast.makeText(getApplicationContext(), "Profile Created", Toast.LENGTH_SHORT).show();
 	            
 	            prefEditor.putString("user_name", responseStatus);
 	            prefEditor.commit();
 	            
 	            Intent settingsPageIntent=new Intent(getApplicationContext(), SettingsActivity.class);
 	            settingsPageIntent.putExtra("first_use", true);
 	        	startActivity(settingsPageIntent);
 	        	
 	        	finish();
 	            
             }
             
        }
 

    }
 
}
