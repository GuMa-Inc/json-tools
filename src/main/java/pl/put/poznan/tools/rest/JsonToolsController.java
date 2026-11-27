package pl.put.poznan.tools.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.put.poznan.tools.logic.JsonMinifier;
import pl.put.poznan.tools.logic.model.InvalidJsonException;
import pl.put.poznan.tools.logic.model.JsonDocument;
import pl.put.poznan.tools.logic.model.JsonLoader;

@RestController
@RequestMapping("/api")
public class JsonToolsController {

    private static final Logger logger = LoggerFactory.getLogger(JsonToolsController.class);

    private final JsonLoader loader = new JsonLoader();
    private final JsonMinifier minifier = new JsonMinifier();

    @RequestMapping(value = "/minimize", method = RequestMethod.POST, produces = "application/json")
    public String minimize(@RequestBody String json) {
        logger.debug(json);

        try {
            JsonDocument document = loader.load(json);
            return minifier.minify(document);
        } catch (InvalidJsonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}