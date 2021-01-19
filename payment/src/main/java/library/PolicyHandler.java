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
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }
    @Autowired
    PaymentRepository paymentRepository;
    
    /*
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_(@Payload Paid paid){

        if(paid.isMe()){
            //추가
            Payment payment = new Payment();
            payment.setId(paid.getId());
            payment.setBookId(paid.getBookId());
            payment.setRentalId(paid.getRentalId());
            payment.setMemberId(paid.getMemberId());
            paymentRepository.save(payment);
            // 결제 성공 로그
            System.out.println("##### paid success : " + paid.toJson());
        }
    }
    */

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelled_(@Payload Cancelled cancelled){

        if(cancelled.isMe()){
            //추가
            Payment payment = new Payment();
            payment.setId(cancelled.getId());
            payment.setBookId(cancelled.getBookId());
            payment.setMemberId(cancelled.getMemberId());
            paymentRepository.save(payment);
            // 취소 성공 로그
            System.out.println("##### cancelled success  : " + cancelled.toJson());
        }
    }

}
