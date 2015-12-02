package geeks.tournamentmaker;

/**
 * Created by Oliver on 12/2/2015.
 */
public class Match {
    private String team1;
    private String team2;
    public Match(String team1, String team2){
        this.team1 = team1;
        this.team2 = team2;
    }
    public String getTeam1(){
        return team1;
    }
    public String getTeam2(){
        return team2;
    }
}
