package com.mhv.batchprocessing.controller;

import com.mhv.batchprocessing.dto.JobStatusResponse;
import com.mhv.batchprocessing.exceptionHandeler.GeneralException;
import com.mhv.batchprocessing.service.customer.CustomerJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
public class CustomerController {

    @Autowired
    private CustomerJobService customerJobService;

    @Value("${customer.file.location.data}")
    private String customerDataLocation;

    private final Logger logger;

    public CustomerController(){
        this.logger = LoggerFactory.getLogger(CustomerController.class);
    }

    @PostMapping("/api/customer/process-and-load")
    public ResponseEntity<JobStatusResponse> validateInputFileAndTriggerCustomerService(@RequestParam("csv-customer-data") MultipartFile multipartFile) throws IOException, MultipartException, GeneralException, JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        if(multipartFile == null || multipartFile.getOriginalFilename() == null){
            throw new GeneralException("Invalid or no file provided");
        }
        String fileName = multipartFile.getOriginalFilename();
        String key = customerJobService.getNewJobKey();
        if(!fileName.endsWith(".csv")){
            throw new GeneralException("Please upload a valid CSV file");
        }else{
            fileName = "customerDataFile_" + key + ".csv";
        }
        Path targetLocation = Paths.get(customerDataLocation).toAbsolutePath().normalize().resolve(fileName);
        try {
            Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e){
            e.printStackTrace();
            logger.info(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JobStatusResponse(e.getMessage()));
        }
        JobStatusResponse jobStatusResponse = customerJobService.triggerCustomerValidationJob(fileName, key);
        return ResponseEntity.status(jobStatusResponse.getResponseCode()).body(jobStatusResponse);
    }
}
