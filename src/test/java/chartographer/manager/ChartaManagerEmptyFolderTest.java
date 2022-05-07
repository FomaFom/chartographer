package chartographer.manager;

import chartographer.constants.ProjectConstants;
import chartographer.dao.ChartaDAO;
import chartographer.exception.ChartaNotFoundException;
import chartographer.exception.CoordinatesOutOfBoundsException;
import chartographer.model.Charta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChartaManagerEmptyFolderTest {

    private static String TEST_FOLDER_PATH = "src/test/resources/testEmptyFolder/";
    private static String TEST_IMAGE_PATH = "src/test/resources/testImage/sample_640×426.bmp";

    private final int TEST_WIDTH = 10000;
    private final int TEST_HEIGHT = 10000;

    private final ChartaManager chartaManager;
    private final ChartaDAO chartaDAO;

    @Autowired
    public ChartaManagerEmptyFolderTest(ChartaDAO chartaDAO, ChartaManager chartaManager) {
        this.chartaDAO = chartaDAO;
        this.chartaManager = chartaManager;
        ReflectionTestUtils.setField(this.chartaDAO, "pathToFiles", TEST_FOLDER_PATH);
        ReflectionTestUtils.setField(this.chartaManager, "pathToFiles", TEST_FOLDER_PATH);
        ReflectionTestUtils.invokeMethod(this.chartaDAO, "scanDirectory", null);
    }

    @Test
    public void createNewChartaTest() throws IOException, ChartaNotFoundException {
        int id = chartaManager.createNewChartas(TEST_WIDTH, TEST_HEIGHT);
        assertNotNull(id);

        Charta charta = chartaDAO.getById(id);
        assertEquals(getMaxPartsInCharta(), charta.getParts().size());
        assertEquals(TEST_WIDTH, charta.getWidth());
        assertEquals(TEST_HEIGHT, charta.getHeight());

        //Cleanup
        chartaManager.deleteChartaById(id);
    }

    @Test
    public void deleteChartaByIdTest() throws IOException, ChartaNotFoundException {
        int id = chartaManager.createNewChartas(TEST_WIDTH, TEST_HEIGHT);
        assertNotNull(id);

        chartaManager.deleteChartaById(id);

        Map<Integer, Charta> chartaMap =
                (HashMap<Integer, Charta>) ReflectionTestUtils
                        .getField(chartaDAO, "chartaMap");

        assertNull(chartaMap.get(id));
    }

    @Test
    public void setAndGetFragmentToChartaTest() throws IOException, ChartaNotFoundException, CoordinatesOutOfBoundsException {
        //Creating charta
        int id = chartaManager.createNewChartas(TEST_WIDTH, TEST_HEIGHT);
        assertNotNull(id);

        //Setting new fragment
        BufferedImage sample = ImageIO.read(new File(TEST_IMAGE_PATH));

        byte[] sampleByteArray;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(sample, ProjectConstants.FILE_FORMAT_BMP, byteArrayOutputStream);
            sampleByteArray = byteArrayOutputStream.toByteArray();
        }

        chartaManager.setFragmentToCharta(id, sampleByteArray, 4800, 4800, 640, 426);

        //Checking new fragment
        byte[] requestedImage = chartaManager.getFragmentFromCharta(id, 4800, 4800, 640, 426);

        assertNotNull(sampleByteArray);
        assertNotNull(requestedImage);
        assertEquals(sampleByteArray.length, requestedImage.length);

        boolean flag = true;
        for (int i = 0; i < sampleByteArray.length; i++) {
            if (sampleByteArray[i] != requestedImage[i]) flag = false;
        }
        assertTrue(flag);

        //Cleanup
        chartaManager.deleteChartaById(id);
    }

    private int getMaxPartsInCharta() {
        int tmpWidth = TEST_WIDTH / ProjectConstants.ChartaPart.MAX_PART_WIDTH;
        int tmpHeight = TEST_HEIGHT / ProjectConstants.ChartaPart.MAX_PART_HEIGHT;
        tmpWidth = TEST_WIDTH % ProjectConstants.ChartaPart.MAX_PART_WIDTH > 0 ? tmpWidth + 1 : tmpWidth;
        tmpHeight = TEST_HEIGHT % ProjectConstants.ChartaPart.MAX_PART_HEIGHT > 0 ? tmpHeight + 1 : tmpHeight;
        return tmpWidth * tmpHeight;
    }
}

