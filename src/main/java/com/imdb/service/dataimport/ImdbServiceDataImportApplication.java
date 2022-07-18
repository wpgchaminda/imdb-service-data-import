package com.imdb.service.dataimport;

import com.imdb.service.dataimport.process.DataImportProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImdbServiceDataImportApplication {

  private static DataImportProcess dataImportProcess;

  @Autowired
  public ImdbServiceDataImportApplication(DataImportProcess dataImportProcess) {
    this.dataImportProcess=dataImportProcess;
  }

  public static void main(String[] args) {
    SpringApplication.run(ImdbServiceDataImportApplication.class, args);

    //Call Data Import
    dataImportProcess.process();
  }

}
