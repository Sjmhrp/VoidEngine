package sjmhrp.gui.text;

import java.io.InputStream;

import sjmhrp.render.Loader;

public class FontType {
	 
    private int textureAtlas;
    private TextMeshCreator loader;

    public FontType(String name) {
    	this(Loader.loadTexture("map/"+name),Class.class.getResourceAsStream("/res/fonts/"+name+".fnt"));
    }
    
    public FontType(int textureAtlas, InputStream fontFile) {
        this.textureAtlas = textureAtlas;
        this.loader = new TextMeshCreator(fontFile);
    }
 
    public int getTextureAtlas() {
        return textureAtlas;
    }
 
    public TextMeshData loadText(GUIText text) {
        return loader.createTextMesh(text);
    }
}