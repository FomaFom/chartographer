package chartographer.controller;

import chartographer.constants.ProjectConstants;
import chartographer.dao.ChartaDAO;
import chartographer.exception.ChartaNotFoundException;
import chartographer.exception.CoordinatesOutOfBoundsException;
import chartographer.manager.ChartaManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChartasControllerTest {

    private static String TEST_EMPTY_FOLDER_PATH = "src/test/resources/testEmptyFolder/";
    private static String TEST_IMAGE_PATH = "src/test/resources/testImage/sample_640×426.bmp";

    private final int TEST_WIDTH = 500;
    private final int TEST_HEIGHT = 500;

    private final ChartaDAO chartaDAO;
    private final ChartaManager chartaManager;
    private final ChartasController chartasController;


    @Autowired
    public ChartasControllerTest(ChartaDAO chartaDAO, ChartaManager chartaManager, ChartasController chartasController) {
        this.chartaDAO = chartaDAO;
        this.chartaManager = chartaManager;
        this.chartasController = chartasController;
        ReflectionTestUtils.setField(this.chartaDAO, "pathToFiles", TEST_EMPTY_FOLDER_PATH);
        ReflectionTestUtils.setField(this.chartaManager, "pathToFiles", TEST_EMPTY_FOLDER_PATH);
        ReflectionTestUtils.invokeMethod(this.chartaDAO, "scanDirectory", null);
    }

    @Test
    public void createNewChartaTest() throws IOException, ChartaNotFoundException {
        ResponseEntity<String> responseEntity = chartasController.createNewCharta(TEST_WIDTH, TEST_HEIGHT);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        //Cleanup
        chartaManager.deleteChartaById(Integer.parseInt(responseEntity.getBody()));
    }

    @Test
    public void savePartTest() throws IOException, ChartaNotFoundException, CoordinatesOutOfBoundsException {
        //Creating charta
        int id = chartaManager.createNewChartas(TEST_WIDTH, TEST_HEIGHT);
        assertNotNull(id);

        BufferedImage sample = ImageIO.read(new File(TEST_IMAGE_PATH));

        byte[] sampleByteArray;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(sample, ProjectConstants.FILE_FORMAT_BMP, byteArrayOutputStream);
            sampleByteArray = byteArrayOutputStream.toByteArray();
        }

        HttpStatus status = chartasController.savePart(id, 110, 110, 640, 426, sampleByteArray).getStatusCode();

        assertEquals(HttpStatus.OK, status);

        //Cleanup
        chartaManager.deleteChartaById(id);
    }

    @Test
    public void getPartTest() throws IOException, ChartaNotFoundException, CoordinatesOutOfBoundsException {
        //Creating charta
        int id = chartaManager.createNewChartas(TEST_WIDTH, TEST_HEIGHT);
        assertNotNull(id);

        ResponseEntity<byte[]> responseEntity = chartasController.getPart(id, 110, 110, 640, 426);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        //Cleanup
        chartaManager.deleteChartaById(id);
    }

    @Test
    public void deleteChartaTest() throws IOException, ChartaNotFoundException {
        //Creating charta
        int id = chartaManager.createNewChartas(TEST_WIDTH, TEST_HEIGHT);
        assertNotNull(id);

        HttpStatus status = chartasController.deleteCharta(id).getStatusCode();

        assertEquals(HttpStatus.OK, status);
    }
}
