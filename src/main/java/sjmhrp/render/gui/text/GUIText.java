package sjmhrp.render.gui.text;

import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.gui.FontPool;
import sjmhrp.render.gui.GUIComponent;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.utils.linear.Vector4d;

public class GUIText extends GUIComponent {

	public static final String DEFAULT_FONT = "verdana";
	
	protected String textString;
	protected double fontSize = 1;
	protected int vertexCount;
	protected Vector3d colour = new Vector3d(1);
	protected double lineMaxSize;
	protected int numberOfLines;
	protected FontType font;
	protected double outlineWidth = 0.6;
	protected Vector3d outlineColour = new Vector3d();
	protected Vector2d dropShadow = new Vector2d();
	protected Vector4d bounds = new Vector4d();
 
	public GUIText(String text) {
		this(text,DEFAULT_FONT);
	}
	
	public GUIText(String text, String font) {
		this(text,font,1);
	}
	
	public GUIText(String text, String font, double maxLineLength) {
        set(text,font,maxLineLength);
        RenderRegistry.registerText(this);
	}
 
	public GUIText set(String text) {
		return set(text,DEFAULT_FONT);
	}
	
	public GUIText set(String text, String font) {
		return set(text,font,1);
	}
	
	public GUIText set(String text, String font, double maxLineLength) {
		if(!RenderHandler.isRenderer()) {
			RenderHandler.addTask(()->set(text,font,maxLineLength));
			return this;
		}
		this.textString = text;
        this.font = FontPool.getFont(font);
        this.lineMaxSize = maxLineLength;
        TextMeshData data = this.font.loadText(this);
        bounds = new Vector4d(1,1,-1,-1);
        for(int i = 0; i < data.getVertexPositions().length; i+=2) {
        	bounds.x=Math.min(bounds.x,data.getVertexPositions()[i]);
        	bounds.y=Math.min(bounds.y,data.getVertexPositions()[i+1]);
        	bounds.z=Math.max(bounds.z,data.getVertexPositions()[i]);
        	bounds.w=Math.max(bounds.w,data.getVertexPositions()[i+1]);
        }
        setModel(Loader.load(data.getVertexPositions(),data.getTextureCoords()));
        this.vertexCount = data.getVertexCount();
		return this;
	}
	
    public GUIText setColour(double r, double g, double b) {
        colour.set(r,g,b);
        return this;
    }
    
    public GUIText setColour(Vector3d c) {
        return setColour(c.x,c.y,c.z);
    }

	public GUIText setFontSize(double size) {
		this.fontSize = size;
    	return this;
	}
	
	public GUIText setOutlineWidth(double width) {
		outlineWidth = width;
		return this;
	}
	
	public GUIText setOutlineColour(Vector3d colour) {
		outlineColour = colour;
		return this;
	}
	
	public GUIText setOutline(double width, Vector3d colour) {
		setOutlineWidth(width);
		return setOutlineColour(colour);
	}
	
	public GUIText setDropShadow(Vector2d offset) {
		dropShadow = offset;
		return this;
	}
	
	public GUIText removeOutline() {
		outlineWidth = 0;
		return this;
	}
	
	public FontType getFont() {
        return font;
    }
    
    public Vector3d getColour() {
        return colour;
    }
 
    public int getNumberOfLines() {
        return numberOfLines;
    }
 
    public int getVertexCount() {
        return vertexCount;
    }
 
    public double getFontSize() {
        return fontSize;
    }
 
    protected void setNumberOfLines(int number) {
        this.numberOfLines = number;
    }
 
    public double getMaxLineSize() {
        return lineMaxSize;
    }
 
    public String getString() {
        return textString;
    }

	public double getOutlineWidth() {
		return outlineWidth;
	}

	public Vector3d getOutlineColour() {
		return outlineColour;
	}

	public Vector2d getDropShadow() {
		return dropShadow;
	}
	
	public Vector4d getBounds() {
		Vector4d bounds = new Vector4d(this.bounds);
		bounds.x=bounds.x*fontSize-0.5*(bounds.x+bounds.z)*(fontSize-1);
		bounds.y=bounds.y*fontSize-0.5*(bounds.y+bounds.w)*(fontSize-1);
		bounds.z=bounds.z*fontSize-0.5*(bounds.x+bounds.z)*(fontSize-1);
		bounds.w=bounds.w*fontSize-0.5*(bounds.y+bounds.w)*(fontSize-1);
		bounds.x+=offset.x;
		bounds.y+=offset.y;
		bounds.z+=offset.x;
		bounds.w+=offset.y;
		return bounds;
	}
}