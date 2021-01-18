package library;

public class Reserved extends AbstractEvent {

    private Long id;
    private Long memberID;
    private Long bookID;
    private String rentalStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getMemberId() {
        return memberID;
    }

    public void setMemberId(Long memberID) {
        this.memberID = memberID;
    }
    public Long getBookId() {
        return bookID;
    }

    public void setBookId(Long bookID) {
        this.bookID = bookID;
    }
    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }
}