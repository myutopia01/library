package library;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Rental_table")
public class Rental {

    private Long id;
    private Long memberId;
    private Long bookId;
    private String rentalStatus;

    @PostPersist
    public void onPostPersist(){
        Cancelled cancelled = new Cancelled();
        BeanUtils.copyProperties(this, cancelled);
        cancelled.publishAfterCommit();


        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        .external.Payment payment = new .external.Payment();
        // mappings goes here
        Application.applicationContext.getBean(.external.PaymentService.class)
            .pay(payment);


        Rentaled rentaled = new Rentaled();
        BeanUtils.copyProperties(this, rentaled);
        rentaled.publishAfterCommit();


        Returned returned = new Returned();
        BeanUtils.copyProperties(this, returned);
        returned.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getRentalStatus() {
        return rentalStatus;
    }

    public void setRentalStatus(String rentalStatus) {
        this.rentalStatus = rentalStatus;
    }




}
