
package library;

public class Paid extends AbstractEvent {

    private Long id;
    private Long bookID;
    private String bookStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {
        return bookID;
    }

    public void setBookId(Long bookID) {
        this.bookID = bookID;
    }
    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }
}
