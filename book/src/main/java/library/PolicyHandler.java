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

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRentaled_(@Payload Rentaled rentaled){

        if(rentaled.isMe()){
            System.out.println("##### listener  : " + rentaled.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRefunded_(@Payload Refunded refunded){

        if(refunded.isMe()){
            System.out.println("##### listener  : " + refunded.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_(@Payload Paid paid){

        if(paid.isMe()){
            System.out.println("##### listener  : " + paid.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReturned_(@Payload Returned returned){

        if(returned.isMe()){
            System.out.println("##### listener  : " + returned.toJson());
        }
    }

}
