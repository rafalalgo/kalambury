package kalambury.controller;

import kalambury.database.Database;
import kalambury.model.*;

/**
 * Created by rafalbyczek on 12.06.16.
 */
public class ZgadnietoHasloHandler {
    public static String zgadnieto(Integer ADD, String word, Client client, AreaDraw areaDraw, TimeLineTask timeLineTask, TipArea tipArea) {
        client.writeToServer("Użytkownik " + client.getName() + " zgadł hasło!");
        client.writeToServer(client.getName() + " + " + ADD.toString() + "!");
        Integer punkty = new Integer(Database.instance.getPoint("SELECT punkty FROM ranking WHERE nazwa = '" + client.getName() + "';"));
        punkty += ADD;
        Database.instance.deletePerson("DELETE FROM ranking WHERE nazwa = '" + client.getName() + "';");
        Database.instance.addPoint("INSERT INTO ranking(nazwa, punkty) VALUES('" + client.getName() + "'," + punkty.toString() + ")");
        word = Password.getWord(word);
        client.writeToServer("Nowa runda! Start!");

        String kto = Database.instance.getWord("SELECT name FROM gracze WHERE rysuje = 1");
        Integer ile = Database.instance.getPoint("SELECT ile_razy FROM gracze WHERE rysuje = 1");
        Database.instance.deletePerson("DELETE FROM gracze WHERE rysuje = 1");
        Database.instance.addPoint("INSERT INTO gracze(name, ile_razy, rysuje) VALUES('" + kto + "', " + new Integer(ile + 1).toString() + ", 0)");

        kto = Database.instance.getWord("SELECT min(name) FROM gracze WHERE ile_razy = (SELECT min(ile_razy) FROM gracze);");
        ile = Database.instance.getPoint("SELECT ile_razy FROM gracze WHERE ile_razy = (SELECT min(ile_razy) FROM gracze) AND name = '" + kto + "'");
        Database.instance.deletePerson("DELETE FROM gracze WHERE name = '" + kto + "'");
        Database.instance.addPoint("INSERT INTO gracze(name, ile_razy, rysuje) VALUES('" + kto + "', " + ile.toString() + ", 1)");

        client.writeToServer("Teraz rysuje " + kto);

        areaDraw.getGraphicsContext2D().clearRect(0, 0, areaDraw.getCanvas().getWidth(), areaDraw.getCanvas().getHeight());
        timeLineTask.getTask().playFromStart();
        tipArea.getTip().setText("Podpowiedź: " + word);
        return word;
    }
}
