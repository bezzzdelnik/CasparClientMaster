package avt.caspar.client.util;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ExcelRead
{
    private XSSFWorkbook book;
    private XSSFSheet sheet;
    private int firstCol;
    private int firstRow;
    private int lastCol;
    private int lastRow = 0;
    private ArrayList<ArrayList> rows = new ArrayList<>();

    public void convertRange(String range) {
        String str = range;
        String[] firstSplit = str.split(":");
        String[] part1 = firstSplit[0].split("(?<=\\D)(?=\\d)");
        String[] part2 = firstSplit[1].split("(?<=\\D)(?=\\d)");

        firstCol = convertLetter(part1[0]);
        System.out.println(firstCol);
        firstRow = Integer.parseInt(part1[1])-1;
        System.out.println(firstRow);
        lastCol = convertLetter(part2[0]);
        System.out.println(lastCol);
        if (part2.length > 1) {
            lastRow = Integer.parseInt(part2[1]);
        }
    }

    public static Integer convertLetter(String str) {
        char[] colstr = str.toCharArray();
        int ii, col=0;
        for(ii=0; ii<colstr.length; ii++) {
            col = 26*col + colstr[ii] - 'A';
        }
        return col;
    }

    public ArrayList<ArrayList> excelReader(String path, String sheetName, String range)
    {
        convertRange(range);
        openBook(path);
        if (book != null) {
            System.out.println ("Книга Excel открыта");
            sheet = book.getSheet(sheetName);
            if (sheet != null) {
                System.out.println ("Страница открыта");
                readCells();
            } else {
                System.out.println ("Страница не найдена");
            }
            try {
                boolean directly = false;
                if (!directly)
                    book.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            System.out.println ("Ошибка чтения файла Excel");
        return rows;
    }

    private void openBook(final String path)
    {
        try {
            File file = new File(path);
            book = (XSSFWorkbook) WorkbookFactory.create(file, "",true);
            book.close();

        } catch (EncryptedDocumentException | IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    private String printCell(XSSFCell cell)
    {
        DataFormatter formatter = new DataFormatter();
        String val;
        // Вывод значения в консоль
        //System.out.print(formatter.formatCellValue(cell) + " / ");
        if (cell.getCellTypeEnum() == CellType.FORMULA) {
            val = cell.getRawValue();
            //System.out.print(cell.getRawValue() + " / ");
        } else {
            val = formatter.formatCellValue(cell);
        }
        return val;
    }

    private void readCells()
    {

        // Определение граничных строк обработки
        int rowStart = firstRow;
        int rowEnd;
        if (lastRow != 0) {
            rowEnd = lastRow;
        } else rowEnd   = sheet.getLastRowNum () + 1;


        for (int rw = rowStart; rw < rowEnd; rw++) {
            ArrayList<String> columns = new ArrayList();
            XSSFRow row = sheet.getRow(rw);
            if (row == null) {
                break;
            }
            int minCol = firstCol;
            int maxCol = lastCol + 1;
            for(int col = minCol; col < maxCol; col++) {
                XSSFCell cell = row.getCell(col);
                if (cell == null) {
                    break;
                }
                columns.add(printCell(cell));
                //printCell(cell);
            }
            rows.add(columns);
            //System.out.println();
        }
    }
}
