package matrix.calculation;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class App {
    private static final String API_KEY = "YOUR_REAL_API_KEY";
    private static final String API_URL = "https://www.thebluealliance.com/api/v3";
    private static final String EVENT_KEY = "2026mnwi"; // Bluff County Regional
    private static final String TEAM_KEY = "frc167";    // Team 167

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        String url = API_URL + "/event/" + EVENT_KEY + "/matches";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-TBA-Auth-Key", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {

            System.out.println("HTTP Status Code: " + response.code());

            if (response.body() != null) {
                String body = response.body().string();

                JSONArray matches = new JSONArray(body);
                List<JSONObject> teamMatches = new ArrayList<>();

                // Filter matches for our team
                for (int i = 0; i < matches.length(); i++) {
                    JSONObject match = matches.getJSONObject(i);
                    JSONObject alliances = match.getJSONObject("alliances");
                    JSONArray redTeams = alliances.getJSONObject("red").getJSONArray("team_keys");
                    JSONArray blueTeams = alliances.getJSONObject("blue").getJSONArray("team_keys");

                    if (containsTeam(redTeams, TEAM_KEY) || containsTeam(blueTeams, TEAM_KEY)) {
                        teamMatches.add(match);
                    }
                }

                // Print a summary of each match
                for (JSONObject match : teamMatches) {
                    String compLevel = match.getString("comp_level");
                    int matchNumber = match.getInt("match_number");

                    JSONObject alliances = match.getJSONObject("alliances");
                    String allianceColor;
                    JSONArray myAlliance, opponentAlliance;

                    if (containsTeam(alliances.getJSONObject("red").getJSONArray("team_keys"), TEAM_KEY)) {
                        allianceColor = "Red";
                        myAlliance = alliances.getJSONObject("red").getJSONArray("team_keys");
                        opponentAlliance = alliances.getJSONObject("blue").getJSONArray("team_keys");
                    } else {
                        allianceColor = "Blue";
                        myAlliance = alliances.getJSONObject("blue").getJSONArray("team_keys");
                        opponentAlliance = alliances.getJSONObject("red").getJSONArray("team_keys");
                    }

                    int myScore = alliances.getJSONObject(allianceColor.toLowerCase()).getInt("score");
                    int opponentScore = alliances.getJSONObject(allianceColor.equals("Red") ? "blue" : "red").getInt("score");

                    System.out.println("Match: " + compLevel.toUpperCase() + " #" + matchNumber);
                    System.out.println("Alliance: " + allianceColor);
                    System.out.println("Teammates: " + jsonArrayToString(myAlliance, TEAM_KEY));
                    System.out.println("Opponents: " + jsonArrayToString(opponentAlliance, null));
                    System.out.println("Score: " + myScore + " - " + opponentScore);
                    System.out.println("Winner: " + match.getString("winning_alliance"));
                    System.out.println("-------------------------------");
                }

            } else {
                System.out.println("No response body received.");
            }

        } catch (IOException e) {
            System.out.println("Request failed:");
            e.printStackTrace();
        }
    }

    // Helper to check if JSONArray contains a team key
    private static boolean containsTeam(JSONArray arr, String teamKey) {
        for (int i = 0; i < arr.length(); i++) {
            if (arr.getString(i).equals(teamKey)) return true;
        }
        return false;
    }

    // Helper to convert JSONArray to string and optionally exclude a team
    private static String jsonArrayToString(JSONArray arr, String exclude) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            String team = arr.getString(i);
            if (exclude == null || !team.equals(exclude)) {
                list.add(team);
            }
        }
        return String.join(", ", list);
    }
}