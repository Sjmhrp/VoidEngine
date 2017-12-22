package sjmhrp.render.gui.text;

import java.util.ArrayList;
import java.util.List;

public class Word {
    
    private List<Character> characters = new ArrayList<Character>();
    private double width = 0;
     
    protected void addCharacter(Character character){
        characters.add(character);
        width += character.getxAdvance();
    }
     
    protected List<Character> getCharacters(){
        return characters;
    }
     
    protected double getWidth(){
        return width;
    }
}