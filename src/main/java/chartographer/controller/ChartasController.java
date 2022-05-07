package chartographer.controller;

import chartographer.constants.ProjectConstants;
import chartographer.exception.ChartaNotFoundException;
import chartographer.exception.CoordinatesOutOfBoundsException;
import chartographer.manager.ChartaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;

@Validated
@RestController
@RequestMapping("/chartas")
public class ChartasController {

    private final ChartaManager chartaManager;

    @Autowired
    public ChartasController(ChartaManager chartaManager) {
        this.chartaManager = chartaManager;
    }

    /**
     * Creating new charta and returns new id.
     *
     * @param width
     * @param height
     * @return Id and response code 201
     * @throws IOException
     */
    @PostMapping()
    public ResponseEntity<String> createNewCharta(
            @RequestParam(ProjectConstants.Controller.WIDTH)
            @Min(ProjectConstants.MINIMAL_SIDE)
            @Max(ProjectConstants.Charta.MAX_CHARTA_WIDTH)
                    int width,

            @RequestParam(ProjectConstants.Controller.HEIGHT)
            @Min(ProjectConstants.MINIMAL_SIDE)
            @Max(ProjectConstants.Charta.MAX_CHARTA_HEIGHT)
                    int height)
            throws IOException {
        String id = chartaManager.createNewChartas(width, height).toString();
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    /**
     * Writes image with specified width and length into desirable charta in specified position
     *
     * @param id     charta id
     * @param x      coordinate
     * @param y      coordinate
     * @param width
     * @param height
     * @param data   byte array of an image we need to write
     * @return Response code 200
     * @throws IOException
     * @throws CoordinatesOutOfBoundsException if specified coordinates is outside of reasonable value or greater then charta size itself
     * @throws ChartaNotFoundException         if requested charta is not exist
     */
    @PostMapping(value = "/{id}", consumes = ProjectConstants.Controller.MIME_TYPE_BMP)
    public ResponseEntity<?> savePart(
            @PathVariable int id,

            @RequestParam(ProjectConstants.Controller.X)
            @Min(ProjectConstants.ChartaPart.MIN_COORDINATE)
                    int x,

            @RequestParam(ProjectConstants.Controller.Y)
            @Min(ProjectConstants.ChartaPart.MIN_COORDINATE)
                    int y,

            @RequestParam(ProjectConstants.Controller.WIDTH)
            @Min(ProjectConstants.MINIMAL_SIDE)
            @Max(ProjectConstants.ChartaPart.MAX_PART_WIDTH)
                    int width,

            @RequestParam(ProjectConstants.Controller.HEIGHT)
            @Min(ProjectConstants.MINIMAL_SIDE)
            @Max(ProjectConstants.ChartaPart.MAX_PART_HEIGHT)
                    int height,

            @RequestBody byte[] data
    )

            throws IOException, CoordinatesOutOfBoundsException, ChartaNotFoundException {

        chartaManager.setFragmentToCharta(id, data, x, y, width, height);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Gets image with specified width and length from desirable charta in specified position
     *
     * @param id
     * @param x      coordinate
     * @param y      coordinate
     * @param width
     * @param height
     * @return Image that is part of charta in desirable place with specified size and response code 200
     * @throws IOException
     * @throws CoordinatesOutOfBoundsException if specified coordinates is outside of reasonable value or greater then charta size itself
     * @throws ChartaNotFoundException         if requested charta is not exist
     */
    @GetMapping(value = "/{id}", produces = ProjectConstants.Controller.MIME_TYPE_BMP)
    public ResponseEntity<byte[]> getPart(
            @PathVariable int id,

            @RequestParam(ProjectConstants.Controller.X)
            @Min(ProjectConstants.ChartaPart.MIN_COORDINATE)
                    int x,

            @RequestParam(ProjectConstants.Controller.Y)
            @Min(ProjectConstants.ChartaPart.MIN_COORDINATE)
                    int y,

            @RequestParam(ProjectConstants.Controller.WIDTH)
            @Min(ProjectConstants.MINIMAL_SIDE)
            @Max(ProjectConstants.ChartaPart.MAX_PART_WIDTH)
                    int width,

            @RequestParam(ProjectConstants.Controller.HEIGHT)
            @Min(ProjectConstants.MINIMAL_SIDE)
            @Max(ProjectConstants.ChartaPart.MAX_PART_HEIGHT)
                    int height
    )

            throws IOException, CoordinatesOutOfBoundsException, ChartaNotFoundException {

        byte[] part = chartaManager.getFragmentFromCharta(id, x, y, width, height);

        return new ResponseEntity<>(part, HttpStatus.OK);
    }

    /**
     * Deletes specified charta
     *
     * @param id charta id
     * @return Response code 200
     * @throws IOException
     * @throws ChartaNotFoundException if requested charta is not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCharta(@PathVariable int id)
            throws IOException, ChartaNotFoundException {

        chartaManager.deleteChartaById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
