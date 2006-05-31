package eraserhead;

import java.awt.*;
import com.golden.gamedev.*;

public class EraseGameEngine extends GameEngine
{
    static final int INTRO = 0;
    static final int HELP = 1;
    static final int PLAY = 2;
    
    long score = 0;
    
    public static void main(String[] args)
    {
        GameLoader game = new GameLoader();
        game.setup(new EraseGameEngine(), new Dimension(640,480), false);
        game.start();
    }

    @Override
    public void initResources()
    {
        nextGameID = INTRO;
    }

    @Override
    public GameObject getGame(int gameID)
    {
        GameObject nextGame = null;
        
        switch(gameID)
        {
            case PLAY:
                score = 0;
                nextGame = new EraseLevelOne(this);
                break;
                
            case INTRO:
                nextGame = new EraseIntro(this);
                break;
                
            case HELP:
                nextGame = new EraseHelp(this);
                break;
        }
        
        return nextGame;
    }

}
