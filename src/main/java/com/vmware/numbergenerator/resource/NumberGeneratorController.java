package com.vmware.numbergenerator.resource;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.numbergenerator.exception.InvalidRequestException;
import com.vmware.numbergenerator.model.NumberGenerator;
import com.vmware.numbergenerator.service.NumberGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vmware.numbergenerator.constants.NumberGeneratorConstants.*;

@RestController
@RequestMapping("/api")
public class NumberGeneratorController {

    @Autowired
    private NumberGeneratorService service;

    @GetMapping(path = "/tasks/{uuid}/status", produces = "application/json")
    public ObjectNode getStatus(@PathVariable String uuid) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put(RESULT, service.getStatus(uuid));
        return objectNode;
    }

    @GetMapping(path = "/tasks/{uuid}", produces = "application/json")
    public <T extends ContainerNode> T getNumberList(@PathVariable String uuid, @RequestParam String action) {

        if (null != action && !action.equals(GET_NUMLIST_PARAM)) {
            throw new InvalidRequestException(INVALID_REQUEST_PARAM);
        }

        List<String> resultList = service.getResult(uuid);
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();

        if (!CollectionUtils.isEmpty(resultList)) {
            if (resultList.size() > 1) {
                ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
                for (String result : resultList) {
                    arrayNode.add(result);
                }
                objectNode.set(RESULTS, arrayNode);
            } else {
                objectNode.put(RESULT, resultList.get(0));
            }
        }

        return (T) objectNode;
    }

    @PostMapping(path = "/generate", consumes = "application/json", produces = "application/json")
    public ResponseEntity generateSequence(@Valid @RequestBody NumberGenerator bean) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("task", service.generateNumberSequence(bean));
        return new ResponseEntity(objectNode, HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/bulkGenerate", consumes = "application/json", produces = "application/json")
    public ResponseEntity generateBulkSequence(@Valid @RequestBody List<NumberGenerator> bean) {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("task", service.generateBulkNumberSequence(bean));
        return new ResponseEntity(objectNode, HttpStatus.ACCEPTED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
