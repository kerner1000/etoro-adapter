package com.github.kerner1000.etoro.stats.io;

import com.github.kerner1000.etoro.stats.model.Transaction;
import com.github.kerner1000.etoro.stats.model.TransactionType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountStatementReader {

    private static final Logger logger = LoggerFactory.getLogger(AccountStatementReader.class);

    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static TransactionType parseType(String typeString) {
        return switch (typeString) {
            case "Open Position" -> TransactionType.OPEN_POSITION;
            case "Profit/Loss of Trade" -> TransactionType.CLOSE_POSITION;
            case "Deposit" -> TransactionType.DEPOSIT;
            default -> TransactionType.UNKNOWN;
        };
    }

    static LocalDateTime getDate(Row row) {
        Cell cell = row.getCell(0);
        String dateString = cell.getStringCellValue();
        return LocalDateTime.parse(dateString, DEFAULT_DATE_TIME_FORMATTER);
    }

    static TransactionType getType(Row row) {
        Cell cell = row.getCell(2);
        String result = cell.getStringCellValue();
        return parseType(result.trim());
    }

    static String getDetails(Row row) {
        Cell cell = row.getCell(3);
        if(cell == null){
            return "";
        }
        return cell.getStringCellValue();
    }

    static BigDecimal getPositionId(Row row) {
        Cell cell = row.getCell(4);
        if (cell == null) {
            return null;
        }
        return new BigDecimal(cell.getStringCellValue());
    }

    static BigDecimal getAmount(Row row) {
        Cell cell = row.getCell(5);
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    static BigDecimal getAccountBalance(Row row) {
        Cell cell = row.getCell(1);
        return BigDecimal.valueOf(cell.getNumericCellValue());
    }

    static Optional<Transaction> handleRow(Row row) {
        LocalDateTime date = getDate(row);
        TransactionType type = getType(row);
        String details = getDetails(row);
        BigDecimal positionId = getPositionId(row);
        BigDecimal amount = getAmount(row);
        BigDecimal accountBalance = getAccountBalance(row);
        if (positionId != null && type != TransactionType.UNKNOWN) {
            Transaction transaction = new Transaction(positionId, amount, date, type, details, accountBalance);
            return Optional.of(transaction);
        }
        return Optional.empty();
    }

    public List<Transaction> readFile(File file) throws IOException {
    return readFile(new FileInputStream(file));
    }

    public List<Transaction> readFile(InputStream stream) throws IOException {
        List<Transaction> result = new ArrayList<>();
        try(XSSFWorkbook workbook = new XSSFWorkbook(stream)) {
            XSSFSheet sheet = workbook.getSheet("Transactions Report");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // skip header
                    continue;
                }
                Optional<Transaction> transaction = handleRow(row);
                transaction.ifPresent(result::add);
            }

        }
        logger.info("File read, {} entries created", result.size());
        return result;
    }
}
