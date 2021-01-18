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
    public void wheneverReturned_(@Payload Returned returned){

        if(returned.isMe()){
            System.out.println("##### listener  : " + returned.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverCancelled_(@Payload Cancelled cancelled){

        if(cancelled.isMe()){
            System.out.println("##### listener  : " + cancelled.toJson());
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_(@Payload Reserved reserved){

        if(reserved.isMe()){
            System.out.println("##### listener  : " + reserved.toJson());
        }
    }

}
