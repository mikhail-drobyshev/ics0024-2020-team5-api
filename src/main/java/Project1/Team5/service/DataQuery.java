package Project1.Team5.service;

import Project1.Team5.service.Dota.Player;
import Project1.Team5.service.Dota.Response;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Data
@Service
public class DataQuery extends Response  {

    RestTemplate restTemplate = new RestTemplate();

    public DotaResponse dataQuery(){

        DotaResponse dotaResponse = new DotaResponse();
        ResponseEntity < List < Player >> rateResponse = restTemplate.exchange("https://api.opendota.com/api/players/100616105/matches?api_key=1d67e82f-c0f0-4e49-bf0d-7a4e2bc537e2", HttpMethod.GET, null, new ParameterizedTypeReference < List < Player >> () {});
        List<Player> players = rateResponse.getBody();

        ArrayList<Integer> heroIDList = new ArrayList<Integer>();
        int winCount = 0;

        for (Player player : players) {
            heroIDList.add(player.getHero_id());
        }

        Map<Integer, Long> heroIDOccurrences =
                heroIDList.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        Integer mostUsedHeroID = heroIDOccurrences.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        Integer leastUsedHeroID = heroIDOccurrences.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
        System.out.println("Most used hero ID:" + mostUsedHeroID);
        System.out.println("Least used hero ID:" + leastUsedHeroID);

        for (Player player : players) {
            if (player.getHero_id() == mostUsedHeroID) {
                //Which slot the player is in. 0-127 are Radiant, 128-255 are Dire
                if (player.isRadiant_win() && player.getPlayer_slot() < 128) {
                    winCount += 1;
                } else if (!player.isRadiant_win() && player.getPlayer_slot() > 127) {
                    winCount += 1;
                }
            }
        }

        double winPercentage = ((double) heroIDOccurrences.get(mostUsedHeroID) - (double) winCount) / (double) heroIDOccurrences.get(mostUsedHeroID);
        System.out.println("Matches played with hero:" + heroIDOccurrences.get(mostUsedHeroID));
        System.out.println("Matches won with hero:" + winCount);
        System.out.println("Matches lost with hero:" + (heroIDOccurrences.get(mostUsedHeroID)-winCount));
        System.out.println("Win %:" + winPercentage);
        return dotaResponse;





    }
}