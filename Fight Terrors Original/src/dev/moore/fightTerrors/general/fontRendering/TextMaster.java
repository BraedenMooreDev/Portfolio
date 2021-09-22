package dev.moore.fightTerrors.general.fontRendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.moore.fightTerrors.general.Loader;
import dev.moore.fightTerrors.general.fontMeshCreator.FontText;
import dev.moore.fightTerrors.general.fontMeshCreator.FontType;
import dev.moore.fightTerrors.general.fontMeshCreator.TextMeshData;

public class TextMaster {

	private Loader loader;
	private Map<FontType, List<FontText>> texts = new HashMap<FontType, List<FontText>>();
	private FontRenderer renderer;

	public TextMaster(Loader loader) {

		renderer = new FontRenderer();
		this.loader = loader;
	}

	public void render() {

		renderer.render(texts);
	}

	public void loadText(FontText text) {

		FontType font = text.getFont();
		TextMeshData data = font.loadText(text);

		int vao = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords());
		text.setMeshInfo(vao, data.getVertexCount());

		List<FontText> textBatch = texts.get(font);

		if (textBatch == null) {

			textBatch = new ArrayList<FontText>();
			texts.put(font, textBatch);
		}

		textBatch.add(text);
	}

	public void removeText(FontText text) {

		List<FontText> textBatch = texts.get(text.getFont());
		textBatch.remove(text);

		if (textBatch.isEmpty())
			texts.remove(text.getFont());
	}

	public void cleanUp() {

		renderer.cleanUp();
	}
}
