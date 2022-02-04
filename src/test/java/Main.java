import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static com.codeborne.selenide.Selenide.*;
import java.io.*;
import java.util.*;

public class Main {

    static String postingService = "https://www.b144.co.il/zipcode.aspx";
    private static final String FILE_NAME = "./src/addresses.xlsx";

    public static void main(String[] args) throws IOException {

        FileInputStream file = new FileInputStream(new File(FILE_NAME));

        // Get the workbook instance for XLS file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        // Get first sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        open(postingService);
        Iterator rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            String cellLocality = row.getCell(0).toString();
            String cellStreet = row.getCell(1).toString();
            String cellHouse = row.getCell(2).toString();

            //avoid of invalid houses numbers
            if (cellHouse.contains("."))
            {
                cellHouse = cellHouse.substring(0, cellHouse.indexOf("."));
            }

            String zipUI = getZipCode(cellLocality, cellStreet, cellHouse);

            if (!zipUI.isEmpty()){
                //add zip code
                XSSFCell cellZip = row.createCell(3);
                cellZip.setCellValue(zipUI);
            }
            else
                //reload page
                open(postingService);

        }

        //save xlsx file
        try {
                FileOutputStream fileOut = new FileOutputStream(FILE_NAME);
                workbook.write(fileOut);
                fileOut.close();
            }
            catch (Exception e) {
                //Catch exception
            }

}

    public static String getZipCode(String locality, String street, String house){

        $("#city_input").setValue(locality);
        $("#street_input").setValue(street);
        $("#houseNum_input").setValue(house);
        $("#b_ZipCodeSearch").click();

        String zipcode = "";

        if ($(".mikudTxt").isDisplayed()){
            String resultText = $(".mikudTxt").getText();
            zipcode = resultText.substring(7, resultText.length());
        }

        return zipcode;
    }
}
