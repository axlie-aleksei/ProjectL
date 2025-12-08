package org.axlie.projectL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
//budet tolko auth service bc legko perenesti methods a launch slozno :(
public class AuthService {

    public String sendToken(String urlStr, String token) {
        try {
            //sobiraem url
            String fullUrl = urlStr + "?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            URL url = new URL(fullUrl);
            //open network connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //chosing method
            conn.setRequestMethod("POST");
            //mi sobiraemsja send data to url
            conn.setDoOutput(true);
            //getinputstream is for get stream of bites from server aka response
            //inputstreamreader convert bytes to text
            //buffer reader do chtenie more udobnim
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            //readline speeds the process by reading big parts of buffer
            String response = in.readLine();
            //closing buffer reader
            in.close();
            return response;

        } catch (IOException e) {
            return "connection error";
        }
    }
    //same as send token
    public String sendPost(String urlStr, String username, String password, boolean rememberMe) {
        try {
            String fullUrl = urlStr + "?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8)
                    + "&rememberMe=" + rememberMe;
            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();
            return response;
        } catch (IOException e) {
            return "Connection error";
        }
    }

    //same as send token razdelenie sdelano tk v send post est boolean remember me
    public String sendPostReg(String urlStr, String username, String password) {
        try {
            String fullUrl = urlStr + "?username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                    + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();
            return response;
        } catch (IOException e) {
            return "Connection error";
        }
    }

}
