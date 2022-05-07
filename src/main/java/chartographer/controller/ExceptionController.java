package chartographer.controller;

import chartographer.exception.ChartaNotFoundException;
import chartographer.exception.CoordinatesOutOfBoundsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.imageio.IIOException;
import javax.validation.ConstraintViolationException;

/**
 * Controller which handles exceptions
 */
@ControllerAdvice
public class ExceptionController {

    /**
     * Handles exception when there is requested charta
     *
     * @return Response code 404
     */
    @ExceptionHandler(ChartaNotFoundException.class)
    public ResponseEntity<?> handleChartaNotFoundException() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exception when specified coordinates is wrong
     *
     * @return Response code 400
     */
    @ExceptionHandler(CoordinatesOutOfBoundsException.class)
    public ResponseEntity<?> handleCoordinatesOutOfBoundException() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exception when specified data is wrong
     *
     * @return Response code 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles exception when there is error in file reading
     *
     * @return Response code 500
     */
    @ExceptionHandler(IIOException.class)
    public ResponseEntity<?> handleIIOException() {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
