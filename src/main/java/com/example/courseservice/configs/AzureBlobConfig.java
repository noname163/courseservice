package com.example.courseservice.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.example.courseservice.utils.EnvironmentVariable;

@Configuration
public class AzureBlobConfig {
   @Autowired
   private EnvironmentVariable environmentVariable;

   @Bean
   public BlobServiceClient clobServiceClient() {

      return new BlobServiceClientBuilder()
            .connectionString(environmentVariable.getAzureStorageEndPoint())
            .buildClient();

   }

   @Bean
   public BlobContainerClient blobContainerClient() {
      return clobServiceClient()
            .getBlobContainerClient("video");

   }
}
