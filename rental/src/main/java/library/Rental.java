package library;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Rental_table")
public class Rental {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long memberId;
    private Long bookId;
    private String rentalStatus;


    // 김성민 postpersist가 아니라 prepersist가 되어야 할 것 같음
    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        library.external.Payment payment = new library.external.Payment();
        // mappings goes here

        // 렌탈ID
        payment.setRentalId(this.id);
        // 사용자ID
        payment.setMemberId(this.memberId);
        // 책 ID
        payment.setBookId(this.bookId);

        RentalApplication.applicationContext.getBean(library.external.PaymentService.class)
            .pay(payment);


    }

    @PostUpdate
    public void onPostUpdate(){

        if (this.rentalStatus.equals("Cancelled") ) {
            Cancelled cancelled = new Cancelled();
            BeanUtils.copyProperties(this, cancelled);
            cancelled.publishAfterCommit();
        }
        else if (this.rentalStatus.equals("Rentaled") ) {
            Rentaled rentaled = new Rentaled();
            BeanUtils.copyProperties(this, rentaled);
            rentaled.publishAfterCommit();
        }
        else if (this.rentalStatus.equals("Returned") ) {
            Returned returned = new Returned();
            BeanUtils.copyProperties(this, returned);
            returned.publishAfterCommit();
        }

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
