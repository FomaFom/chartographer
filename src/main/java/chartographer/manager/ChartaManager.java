package chartographer.manager;

import chartographer.Main;
import chartographer.constants.ProjectConstants;
import chartographer.dao.ChartaDAO;
import chartographer.dao.ChartaPartDAO;
import chartographer.exception.ChartaNotFoundException;
import chartographer.exception.CoordinatesOutOfBoundsException;
import chartographer.model.Charta;
import chartographer.model.ChartaPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ChartaManager {

    private final String pathToFiles;

    private final ChartaDAO chartaDAO;
    private final ChartaPartDAO chartaPartDAO;

    @Autowired
    public ChartaManager(ChartaDAO chartaDAO, ChartaPartDAO chartaPartDAO) {
        this.chartaPartDAO = chartaPartDAO;
        this.chartaDAO = chartaDAO;
        pathToFiles = Main.pathToFiles;
    }

    /**
     * Creates new Charta with specified parameters and it's new id
     *
     * @param width
     * @param height
     * @return Id of created Charta
     * @throws IOException
     */
    public synchronized Integer createNewChartas(int width, int height) throws IOException {

        Charta charta = new Charta(width, height);
        chartaDAO.save(charta);

        for (int y = 0; y < height; y += Math.min(ProjectConstants.ChartaPart.MAX_PART_HEIGHT, height - y)) {
            for (int x = 0; x < width; x += Math.min(ProjectConstants.ChartaPart.MAX_PART_WIDTH, width - x)) {
                ChartaPart chartaPart = chartaPartDAO.save(new chartographer.model.ChartaPart(x, y,
                        Math.min(ProjectConstants.ChartaPart.MAX_PART_WIDTH, width - x),
                        Math.min(ProjectConstants.ChartaPart.MAX_PART_HEIGHT, height - y)), charta);

                File file = new File(
                        getPathByChartaPart(chartaPart));

                BufferedImage image = new BufferedImage(chartaPart.getWidth(), chartaPart.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

                ImageIO.write(image, ProjectConstants.FILE_FORMAT_BMP, file);
            }
        }

        return charta.getId();
    }

    /**
     * Deletes charta with specified id
     *
     * @param id
     * @throws IOException
     * @throws ChartaNotFoundException if there is no such Charta
     */
    public synchronized void deleteChartaById(int id) throws IOException, ChartaNotFoundException {
        Charta charta = chartaDAO.getById(id);
        for (ChartaPart cp : charta.getParts()) {
            Files.delete(Path.of(getPathByChartaPart(cp)));
        }

        chartaDAO.deleteById(id);
    }

    /**
     * Gets image with specified width and length from desirable charta in specified position
     *
     * @param id     Charta id
     * @param x      coordinate
     * @param y      coordinate
     * @param width
     * @param height
     * @return byte array from created image
     * @throws IOException
     * @throws ChartaNotFoundException         if requested charta is not exist
     * @throws CoordinatesOutOfBoundsException if specified coordinates is outside of reasonable value or greater then charta size itself
     */
    public synchronized byte[] getFragmentFromCharta(int id, int x, int y, int width, int height)
            throws IOException, ChartaNotFoundException, CoordinatesOutOfBoundsException {

        Charta charta = chartaDAO.getById(id);

        if (charta.getHeight() < y || charta.getWidth() < x) {
            throw new CoordinatesOutOfBoundsException(
                    ProjectConstants.Exception.COORDINATES_OUT_OF_BOUNDS_EXCEPTION_MESSAGE);
        }

        BufferedImage imageToPass = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        List<ChartaPart> chartaParts = chartaPartDAO.getCrossedChartaParts(charta, x, y, width, height);

        for (ChartaPart cp : chartaParts) {
            int startX = Math.max(x, cp.getX());
            int startY = Math.max(y, cp.getY());
            int endX = Math.min(x + width, cp.getX() + cp.getWidth());
            int endY = Math.min(y + height, cp.getY() + cp.getHeight());

            BufferedImage imageToWriteFrom =
                    ImageIO.read(new File(getPathByChartaPart(cp)))
                            .getSubimage(startX - cp.getX(), startY - cp.getY(), endX - startX, endY - startY);

            imageToPass
                    .createGraphics()
                    .drawImage(imageToWriteFrom, startX - x, startY - y, endX - startX, endY - startY, null);
        }

        byte[] result;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(imageToPass, ProjectConstants.FILE_FORMAT_BMP, byteArrayOutputStream);
            result = byteArrayOutputStream.toByteArray();
        }

        return result;
    }

    /**
     * Writes image with specified width and length into desirable charta in specified position
     *
     * @param id     Charta id
     * @param data   byte massive of image that need to be written into charta
     * @param x      coordinate
     * @param y      coordinate
     * @param width
     * @param height
     * @throws IOException
     * @throws ChartaNotFoundException         if requested charta is not exist
     * @throws CoordinatesOutOfBoundsException if specified coordinates is outside of reasonable value or greater then charta size itself
     */
    public synchronized void setFragmentToCharta(int id, byte[] data, int x, int y, int width, int height)
            throws IOException, ChartaNotFoundException, CoordinatesOutOfBoundsException {
        Charta charta = chartaDAO.getById(id);

        if (charta.getHeight() < y || charta.getWidth() < x) {
            throw new CoordinatesOutOfBoundsException(
                    ProjectConstants.Exception.COORDINATES_OUT_OF_BOUNDS_EXCEPTION_MESSAGE);
        }

        BufferedImage imageFromData;

        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            imageFromData = ImageIO.read(stream);
        }

        BufferedImage imageToWrite = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        imageToWrite.createGraphics()
                .drawImage(
                        imageFromData,
                        0,
                        0,
                        imageFromData.getWidth(),
                        imageFromData.getHeight(),
                        null
                );

        List<ChartaPart> chartaParts = chartaPartDAO.getCrossedChartaParts(charta, x, y, width, height);

        for (ChartaPart cp : chartaParts) {
            int startX = Math.max(x, cp.getX());
            int startY = Math.max(y, cp.getY());
            int endX = Math.min(x + width, cp.getX() + cp.getWidth());
            int endY = Math.min(y + height, cp.getY() + cp.getHeight());

            File f = new File(getPathByChartaPart(cp));

            BufferedImage imagePart = imageToWrite.getSubimage(
                    startX - x,
                    startY - y,
                    endX - startX,
                    endY - startY
            );

            BufferedImage imageToBeWritten = ImageIO.read(f);
            imageToBeWritten
                    .createGraphics()
                    .drawImage(imagePart,
                            startX - cp.getX(),
                            startY - cp.getY(),
                            endX - startX,
                            endY - startY,
                            null
                    );

            ImageIO.write(imageToBeWritten, ProjectConstants.FILE_FORMAT_BMP, f);

        }
    }

    /**
     * @param chartaPart {@link ChartaPart}
     * @return Image path that leads to file which is described by specified ChartaPart
     */
    private String getPathByChartaPart(ChartaPart chartaPart) {
        return String.format("%s_%s_%s_%s.%s",
                pathToFiles + chartaPart.getChartaId(),
                chartaPart.getId(),
                chartaPart.getX(),
                chartaPart.getY(),
                ProjectConstants.FILE_FORMAT_BMP);
    }
}
