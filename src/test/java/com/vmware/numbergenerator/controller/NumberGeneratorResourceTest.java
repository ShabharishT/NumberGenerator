package com.vmware.numbergenerator.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vmware.numbergenerator.exception.InvalidRequestException;
import com.vmware.numbergenerator.model.NumberGenerator;
import com.vmware.numbergenerator.resource.NumberGeneratorController;
import com.vmware.numbergenerator.service.NumberGeneratorService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static com.vmware.numbergenerator.constants.NumberGeneratorConstants.*;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class NumberGeneratorResourceTest {

    @InjectMocks
    NumberGeneratorController controller;

    @Spy
    NumberGeneratorService service;

    @BeforeEach
    public void beforeEach() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    public void testGenerateSequence() {
        NumberGenerator numberGenerator = new NumberGenerator("10", "2");
        ResponseEntity responseEntity = controller.generateSequence(numberGenerator);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(202);
        assertThat(((ObjectNode)responseEntity.getBody()).get("task")).isNotNull();
    }

    @Test
    public void testGenerateBulkSequence() {
        List<NumberGenerator> beanList = new ArrayList<>();
        beanList.add(new NumberGenerator("10", "2"));
        beanList.add(new NumberGenerator("100", "3"));
        ResponseEntity responseEntity = controller.generateBulkSequence(beanList);

        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(202);
        assertThat(((ObjectNode)responseEntity.getBody()).get("task")).isNotNull();
    }

    @Test
    public void testGetStatus() {
        List<String> list = new ArrayList<>();
        list.add("{10,8,6,4,2,0}");
        NumberGeneratorService.cache.put("UUID", list);
        ObjectNode objectNode = controller.getStatus("UUID");

        assertThat(objectNode.get(RESULT).textValue()).isEqualTo(SUCCESS);
    }

    @Test
    public void testGetStatusInProgress() {
        NumberGeneratorService.cache.put("UUID", null);
        ObjectNode objectNode = controller.getStatus("UUID");

        assertThat(objectNode.get(RESULT).textValue()).isEqualTo(IN_PROGRESS);
    }

    @Test
    public void testGetStatusFailure() {
        NumberGeneratorService.cache.put("UUID", new ArrayList<>());
        ObjectNode objectNode = controller.getStatus("UUID-1");

        assertThat(objectNode.get(RESULT).textValue()).isEqualTo(ERROR);
    }

    @Test
    public void testGetNumberList() {
        List<String> list = new ArrayList<>();
        list.add("{10,8,6,4,2,0}");
        list.add("{3,2,1}");
        NumberGeneratorService.cache.put("UUID", list);
        ObjectNode objectNode = controller.getNumberList("UUID", GET_NUMLIST_PARAM);

        assertThat(objectNode.get(RESULTS).get(0).textValue()).isEqualTo("{10,8,6,4,2,0}");
        assertThat(objectNode.get(RESULTS).get(1).textValue()).isEqualTo("{3,2,1}");
    }

    @Test
    public void testGetNumberListWithOneResult() {
        List<String> list = new ArrayList<>();
        list.add("{10,8,6,4,2,0}");
        NumberGeneratorService.cache.put("UUID", list);
        ObjectNode objectNode = controller.getNumberList("UUID", GET_NUMLIST_PARAM);

        assertThat(objectNode.get(RESULT).textValue()).isEqualTo("{10,8,6,4,2,0}");
    }

    @Test
    public void testGetNumberListWithInvalidParam() {
        List<String> list = new ArrayList<>();
        list.add("{10,8,6,4,2,0}");
        NumberGeneratorService.cache.put("UUID", list);
        try {
            controller.getNumberList("UUID", "get_num");
        } catch (RuntimeException e) {
            assertThat(e instanceof InvalidRequestException).isTrue();
        }
    }
}
