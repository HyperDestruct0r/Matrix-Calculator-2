package matrix.calculation;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import io.github.cdimascio.dotenv.Dotenv;


public class App {
    private static final String API_URL = "https://www.thebluealliance.com/api/v3";
    public static void main(String[] args) throws IOException{
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");
        OkHttpClient client = new OkHttpClient();
        final String STATUS_URL = API_URL + "/team/frc167/events";
        Request request = new Request.Builder()
          .addHeader("X-TBA-Auth-Key", apiKey)
          .url(STATUS_URL).build();
        try(Response response = client.newCall(request).execute()){
            System.out.println(response.body().string());
        }
    }
}
