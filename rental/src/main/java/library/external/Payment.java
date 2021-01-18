package library.external;

public class Payment {

    private Long id;
    private Long bookId;
    private String bookStatus;
    private Long memberId;
    private Long rentalId;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {
        return bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getBookStatus() {
        return bookStatus;
    }
    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }
    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public Long getRentalId() {
        return rentalId;
    }
    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

}
