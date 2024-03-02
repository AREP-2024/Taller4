package edu.escuelaing.arep.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpConnection {
    private static final String USER_AGENT = "Mozilla/5.0";
    private final String url;
    private final String key;
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public HttpConnection(String url, String key) {
        this.url = url;
        this.key = key;
    }

    public String getUrl(){
        return url;
    }

    public String getKey(){
        return key;
    }
    /* 
        * Trae la informacion de la pelicula
        * @param nameMovie nombre de la pelicula que se desea consultar
        * @return String con la informacion de la pelicula    
     */     
    public String infoMovie(String nameMovie) throws IOException {

        URL obj = new URL(url+"?apikey="+key+"&t="+nameMovie);

        System.err.println(nameMovie);
        
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        
        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
             return response.toString();
        } else {
            System.out.println("GET request not worked");
        }
        return null;
    }

    /*
        * Trae la informacion de la pelicula con cache
        * @param nameMovie nombre de la pelicula que se desea consultar
        * @return String con la informacion de la pelicula
    */
    public String infoMovieWithCache(String nameMovie) throws IOException {
        // Check if the movie information is already in the cache
        if (cache.containsKey(nameMovie)) {
            System.out.println("Cache hit for movie: " + nameMovie);
            return cache.get(nameMovie);
        }

        // If not in cache, make the API call and store the result in the cache
        String movieInfo = infoMovie(nameMovie);
        cache.put(nameMovie, movieInfo);
        return movieInfo;
    }
  
}
