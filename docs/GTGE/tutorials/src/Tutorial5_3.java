// GTGE
import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;


/**
 * Applet game launcher.
 *
 * Objective: show how to subclass GameLoader class in order to run a game in
 *            applet environment
 */
public class Tutorial5_3 extends GameLoader {


    protected Game createAppletGame() {
        return new Tutorial5_1();
    }

}