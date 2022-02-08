package com.challenge.pdftotext;

import com.challenge.pdftotext.controller.StorageController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StorageController.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StorageControllerTest {

    private final String sampleFileName = "sample.pdf";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StorageController storageController;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void uploadFileOkResponse() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", sampleFileName, MediaType.APPLICATION_PDF_VALUE,
                "Hello, World!".getBytes());
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockMvc.perform(multipart("/file/upload").file(file)).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void downloadFileOkResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MvcResult result =
                mockMvc.perform(MockMvcRequestBuilders.get("/file/download/" + sampleFileName)).andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());
    }
}