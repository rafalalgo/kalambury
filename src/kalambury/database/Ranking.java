package kalambury.database;

/**
 * Created by rafalbyczek on 12.06.16.
 */
public class Ranking {
    private String nazwa;
    private int punkty;

    public Ranking(String nazwa, int punkty) {
        this.nazwa = nazwa;
        this.punkty = punkty;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public int getPunkty() {
        return punkty;
    }

    public void setPunkty(int punkty) {
        this.punkty = punkty;
    }

}
