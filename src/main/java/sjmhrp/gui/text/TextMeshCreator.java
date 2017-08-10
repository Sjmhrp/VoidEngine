package sjmhrp.gui.text;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sjmhrp.linear.Vector2d;

public class TextMeshCreator {
	 
    protected static final double LINE_HEIGHT = 0.03f;
    protected static final int SPACE_ASCII = 32;
 
    private MetaFile metaData;
 
    protected TextMeshCreator(InputStream metaFile) {
        metaData = new MetaFile(metaFile);
    }
 
    protected TextMeshData createTextMesh(GUIText text) {
        List<Line> lines = createStructure(text);
        TextMeshData data = createQuadVertices(text, lines);
        return data;
    }
 
    private List<Line> createStructure(GUIText text) {
        char[] chars = text.getString().toCharArray();
        List<Line> lines = new ArrayList<Line>();
        Line currentLine = new Line(metaData.getSpaceWidth(),text.getMaxLineSize());
        Word currentWord = new Word();
        for (char c : chars) {
            int ascii = (int) c;
            if (ascii == SPACE_ASCII) {
                boolean added = currentLine.attemptToAddWord(currentWord);
                if (!added) {
                    lines.add(currentLine);
                    currentLine = new Line(metaData.getSpaceWidth(),text.getMaxLineSize());
                    currentLine.attemptToAddWord(currentWord);
                }
                currentWord = new Word();
                continue;
            }
            Character character = metaData.getCharacter(ascii);
            currentWord.addCharacter(character);
        }
        completeStructure(lines, currentLine, currentWord, text);
        return lines;
    }
 
    private void completeStructure(List<Line> lines, Line currentLine, Word currentWord, GUIText text) {
        boolean added = currentLine.attemptToAddWord(currentWord);
        if (!added) {
            lines.add(currentLine);
            currentLine = new Line(metaData.getSpaceWidth(),text.getMaxLineSize());
            currentLine.attemptToAddWord(currentWord);
        }
        lines.add(currentLine);
    }
 
    private TextMeshData createQuadVertices(GUIText text, List<Line> lines) {
        text.setNumberOfLines(lines.size());
        double curserX = 0f;
        double curserY = 0f;
        List<Double> vertices = new ArrayList<Double>();
        List<Double> textureCoords = new ArrayList<Double>();
        for (Line line : lines) {
            for (Word word : line.getWords()) {
                for (Character letter : word.getCharacters()) {
                    addVerticesForCharacter(curserX, curserY, letter, vertices);
                    addTexCoords(textureCoords, letter.getxTextureCoord(), letter.getyTextureCoord(),
                            letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
                    curserX += letter.getxAdvance();
                }
                curserX += metaData.getSpaceWidth();
            }
            curserX = 0;
            curserY += LINE_HEIGHT;
        }
        Vector2d min = new Vector2d(1);
        Vector2d max = new Vector2d(-1);
        for(int i = 0; i < vertices.size(); i+=2) {
        	min.x=Math.min(min.x,vertices.get(i));
        	min.y=Math.min(min.y,vertices.get(i+1));
        	max.x=Math.max(max.x,vertices.get(i));
        	max.y=Math.max(max.y,vertices.get(i+1));
        }
        Vector2d offset = max.add(min).scale(0.5);
        for(int i = 0; i < vertices.size(); i+=2) {
        	vertices.set(i,vertices.get(i)-offset.x);
        	vertices.set(i+1,vertices.get(i+1)-offset.y);
        }
        return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
    }
 
	private void addVerticesForCharacter(double curserX, double curserY, Character character,List<Double> vertices) {
		double x = curserX + character.getxOffset();
		double y = curserY + character.getyOffset();
		double maxX = x + character.getSizeX();
		double maxY = y + character.getSizeY();
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}
 
    private static void addVertices(List<Double> vertices, double x, double y, double maxX, double maxY) {
		vertices.add(x);
		vertices.add(y);
		vertices.add(x);
		vertices.add(maxY);
		vertices.add(maxX);
		vertices.add(maxY);
		vertices.add(maxX);
		vertices.add(maxY);
		vertices.add(maxX);
		vertices.add(y);
		vertices.add(x);
		vertices.add(y);
	}

	private static void addTexCoords(List<Double> texCoords, double x, double y, double maxX, double maxY) {
		texCoords.add(x);
		texCoords.add(y);
		texCoords.add(x);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(y);
		texCoords.add(x);
		texCoords.add(y);
	}
 
     
    private static double[] listToArray(List<Double> list) {
    	double[] array = new double[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}