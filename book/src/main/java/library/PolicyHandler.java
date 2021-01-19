package library;

import library.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    // kjh
    @Autowired BookRepository bookRepository;



    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRentaled_(@Payload Rentaled rentaled){

        if(rentaled.isMe() && rentaled.getBookId()!=null){
            System.out.println("##### listener  : " + rentaled.toJson());
            // Correlation id 는 'bookID' 임
            bookRepository.findById(Long.valueOf(rentaled.getBookId())).ifPresent((book)->{
                book.setBookStatus(rentaled.getRentalStatus());
                book.setMemberId(rentaled.getMemberId());
                //rental ID는 일단 bookID로 ...
                book.setRendtalId(rentaled.getBookId());
                bookRepository.save(book);
            });

        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRefunded_(@Payload Refunded refunded){

        if(refunded.isMe()  && refunded.getBookId()!=null ){
            System.out.println("##### listener  : " + refunded.toJson());
            bookRepository.findById(Long.valueOf(refunded.getBookId())).ifPresent((book)->{
                book.setBookStatus("refunded");
                book.setMemberId(null);
                book.setRendtalId(null);
                bookRepository.save(book);
            });
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_(@Payload Paid paid){

        if(paid.isMe() && paid.getBookId()!=null){
            System.out.println("##### listener  : " + paid.toJson());
            bookRepository.findById(Long.valueOf(paid.getBookId())).ifPresent((book)->{
                book.setBookStatus("paid");
//                book.setMemberId(null);
//                book.setRendtalId(null);
                bookRepository.save(book);
            });


        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReturned_(@Payload Returned returned){

        if(returned.isMe() && returned.getBookId()!=null){
            System.out.println("##### listener  : " + returned.toJson());
            bookRepository.findById(Long.valueOf(returned.getBookId())).ifPresent((book)->{
                book.setBookStatus("returned");
                book.setMemberId(null);
                book.setRendtalId(null);
                bookRepository.save(book);
            });
        }
    }

}
