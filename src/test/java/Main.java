import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static com.codeborne.selenide.Selenide.*;
import static io.restassured.RestAssured.given;

import java.io.*;
import java.util.*;

public class Main {

    static String postingService = "https://www.b144.co.il/zipcode.aspx";

    //input filename here
    //it is not stable working with tables about 100 rows
    // please, use tables about 50 rows
    static String fileName = "borrower_addresses_1-50";
    private static final String INPUT_FILE_NAME = "./src/"+fileName+".xlsx";
    private static final String OUTPUT_FILE_NAME = "./src/zip_UI_"+fileName+".xlsx";

    public static void main(String[] args) throws IOException {

        FileInputStream file = new FileInputStream(new File(INPUT_FILE_NAME));

        // Get the workbook instance for XLS file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        // Get first sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        //open(postingService);
        Iterator rowIterator = sheet.rowIterator();



        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();


            String cellLocality = "";
            String cellStreet = "";
            String cellHouse = "";
            try{

            cellLocality = row.getCell(1).toString();
            cellStreet = row.getCell(2).toString();
            cellHouse = row.getCell(3).toString();
            }
            catch (Exception e){}



            //avoid of invalid houses numbers
            if (cellHouse.contains("."))
            {
                cellHouse = cellHouse.substring(0, cellHouse.indexOf("."));
            }

            String zipUI = getZipUI(cellLocality, cellStreet, cellHouse, postingService);

            if (!zipUI.isEmpty()){
                //add zip code
                XSSFCell cellZip = row.createCell(4);
                cellZip.setCellValue(zipUI);
            }
            System.out.println(zipUI);


        }

        //save xlsx file
        try {
                FileOutputStream fileOut = new FileOutputStream(OUTPUT_FILE_NAME);
                workbook.write(fileOut);
                fileOut.close();
            }
            catch (Exception e) {
                //Catch exception
            }
}


    public static String getZipUI(String locality, String street, String house, String site){

        open(site);
        $("#city_input").setValue(locality);
        $("#street_input").setValue(street);
        $("#houseNum_input").setValue(house);
        $("#b_ZipCodeSearch").click();

        String zipcode = "";

        if ($$(".mikudTxt").get(0).isDisplayed()){
            String resultText = $(".mikudTxt").getText();
            zipcode = resultText.substring(7, resultText.length());
        }

        if ($$(".mikudTxt").get(1).isDisplayed()){
            int i=1;
                while ($$(".mikudTxt").get(i).isDisplayed()){
                    String resultText = $$(".mikudTxt").get(i).getText();
                    zipcode = zipcode + ", "+resultText.substring(7, resultText.length());;
                    i++;
            }
        }
        return zipcode;
    }

}

