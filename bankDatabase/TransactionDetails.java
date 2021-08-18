package bankDatabase;

import java.math.BigDecimal;

public class TransactionDetails {
    private int customerId;
    private long accountNumber;
    private BigDecimal transactionAmount;
    private long transactionNumber;
    private String transactionType;

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setTransactionNumber(long transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getCustomerId() {
        return customerId;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public long getTransactionNumber() {
        return transactionNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }
}
