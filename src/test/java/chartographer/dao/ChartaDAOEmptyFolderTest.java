package chartographer.dao;

import chartographer.manager.ChartaManager;
import chartographer.model.Charta;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ChartaDAOEmptyFolderTest {

    private static String TEST_EMPTY_FOLDER_PATH = "src/test/resources/testEmptyFolder/";

    private final ChartaDAO chartaDAO;
    private final ChartaManager chartaManager;


    @Autowired
    public ChartaDAOEmptyFolderTest(ChartaDAO chartaDAO, ChartaManager chartaManager) {
        this.chartaDAO = chartaDAO;
        this.chartaManager = chartaManager;
        ReflectionTestUtils.setField(this.chartaDAO, "pathToFiles", TEST_EMPTY_FOLDER_PATH);
        ReflectionTestUtils.setField(this.chartaManager, "pathToFiles", TEST_EMPTY_FOLDER_PATH);
        ReflectionTestUtils.invokeMethod(this.chartaDAO,"scanDirectory", null );
    }

    @Test
    public void emptyChartaMapTest() {
        Map<Integer, Charta> chartaMap =
                (HashMap<Integer, Charta>) ReflectionTestUtils
                        .getField(chartaDAO, "chartaMap");
        assertEquals(0, chartaMap.size());
    }

}

