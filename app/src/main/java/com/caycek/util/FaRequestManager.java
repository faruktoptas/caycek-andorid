package com.caycek.util;

/**
 * Created by faruktoptas on 22/09/16.
 */

import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class FaRequestManager {


    private static final FaRequestManager myInstance = new FaRequestManager();

    public static FaRequestManager getInstance() {
        return myInstance;
    }

    public void makePostRequest(String url, String headerJson, String bodyJson, OnFaResponseListener faResponseListener) {
        FaRequest faRequest = new FaRequest("POST", faResponseListener);
        faRequest.setHeaderJson(headerJson);
        faRequest.setBodyJson(bodyJson);
        faRequest.execute(url);

    }

    public void makeGetRequest(String url, String headerJson, OnFaResponseListener faResponseListener) {
        FaRequest faRequest = new FaRequest("GET", faResponseListener);
        faRequest.setHeaderJson(headerJson);
        faRequest.execute(url);
    }

    private static class FaRequest extends AsyncTask<String, Integer, FaResponse> {

        private final OnFaResponseListener mOnFaResponseListener;
        private final String mHttpMethod;
        private String mHeaderJson;
        private String mBodyJson;

        public FaRequest(String httpMethod, OnFaResponseListener faResponseListener) {
            mOnFaResponseListener = faResponseListener;
            mHttpMethod = httpMethod;
        }


        @Override
        protected FaResponse doInBackground(String... params) {
            FaResponse faResponse = new FaResponse();
            InputStream inputStream = null;
            try {
                java.net.URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setDoInput(true);
                if (httpURLConnection instanceof HttpsURLConnection) {
                    ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                    ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    });
                }
                httpURLConnection.setRequestMethod(mHttpMethod);
                if (mHeaderJson != null && mHeaderJson.length() > 0) {
                    try {
                        JSONObject header = new JSONObject(mHeaderJson);
                        Iterator<String> iterator = header.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            String value = header.getString(key);
                            httpURLConnection.addRequestProperty(key, value);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                }

                if (mHttpMethod.equals("POST") && mBodyJson != null) {
                    OutputStream os = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(mBodyJson);
                    writer.flush();
                    writer.close();
                    os.close();
                }

                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }
                String response = readInputStream(inputStream);
                faResponse.setResponse(response);
                faResponse.setCode(responseCode);
                return faResponse;
            } catch (Exception e) {
                e.printStackTrace();
                faResponse.setResponse(e.getMessage());
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return faResponse;
        }


        @Override
        protected void onPostExecute(FaResponse faResponse) {
            super.onPostExecute(faResponse);
            mOnFaResponseListener.onResponse(faResponse);
        }

        private String readInputStream(InputStream stream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }

        public void setHeaderJson(String headerJson) {
            mHeaderJson = headerJson;
        }

        public void setBodyJson(String bodyJson) {
            mBodyJson = bodyJson;
        }

    }

    public interface OnFaResponseListener {
        void onResponse(FaResponse faResponse);
    }

    public static class FaResponse {
        private int code = 0;
        private String response = "";

        public FaResponse() {
        }

        public int getCode() {
            return code;
        }

        public String getResponse() {
            return response;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        public String toString() {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Response", response);
                jsonObject.put("Code", code);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}