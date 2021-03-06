package kalambury.controller.game;

import kalambury.database.Database;
import kalambury.model.client.Client;
import kalambury.model.dictionary.Password;
import kalambury.model.gui.AreaDraw;
import kalambury.model.gui.TimeLineTask;
import kalambury.model.gui.TipArea;

/**
 * Created by rafalbyczek on 14.06.16.
 */
public class MinalCzasController {
    public static String make_it(Integer ADD, String word, Client client, AreaDraw areaDraw, TimeLineTask timeLineTask, TipArea tipArea, String aktDraw) {
        client.writeToServer("Użytkownik " + client.getName() + " rysował za długo!");
        client.writeToServer("Użytkownik " + client.getName() + " -50 punktów");
        client.writeToServer("Nikt nie zgadł hasła - " + word);
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
        areaDraw.safeToFile();

        Database.instance.changeTime("DELETE FROM czas;");
        Database.instance.changeTime("INSERT INTO czas(czas) VALUES ('0')");
        Database.instance.addPoint("DELETE FROM tip;");
        Database.instance.addPoint("INSERT INTO tip(ktora) VALUES(1);");

        timeLineTask.getTask().playFromStart();
        tipArea.getTip().setText("Podpowiedź: " + Password.getHint(word));
        return word;
    }
}
