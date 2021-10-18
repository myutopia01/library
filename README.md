# 도서 예약 서비스
 
# Table of contents
 
 - [도서 예약 서비스](#---)
   - [서비스 시나리오](#서비스-시나리오)  
   - [분석/설계](#분석설계)
   - [구현](#구현)
    
# 서비스 시나리오
 
 기능적 요구사항
 1. 사용자가 도서를 예약한다.
 1. 도서 예약 시 결제가 완료되어야 한다.
 1. 사용자가 예약 중인 도서를 대여 처리한다.
 1. 사용자가 대여 중인 도서를 반납 처리한다.
 1. 사용자가 예약을 취소할 수 있다.
 1. 도서 예약 취소 시에는 결제가 취소된다.
 1. 사용자가 예약/대여 상태를 확인할 수 있다.
 
 비기능적 요구사항
 1. 트랜잭션
     1. 결제가 되지 않은 경우 예약할 수 없다 (Sync 호출)
 1. 장애격리
     1. 도서관리 기능이 수행되지 않더라도 대여/예약은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
     1. 결제 시스템이 과중되면 사용자를 잠시동안 받지 않고 예약을 잠시후에 하도록 유도한다  Circuit breaker, fallback
 1. 성능
     1. 사용자는 MyPage에서 본인 예약 및 대여 도서의 목록과 상태를 확인할 수 있어야한다 CQRS
 
 
 # 분석/설계
 
## Event Storming 결과
 * MSAEz 로 모델링한 이벤트스토밍 결과: 
 ![image](https://user-images.githubusercontent.com/53402465/104991785-e6c69180-5a62-11eb-9478-19b0582d4201.PNG)  



## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/53402465/104991783-e5956480-5a62-11eb-91e6-69020468ab61.PNG)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd rental (port:8081)
mvn spring-boot:run

cd payment (port:8082)
mvn spring-boot:run  

cd book (port:8084)
mvn spring-boot:run

cd mypage (port:8083)
mvn spring-boot:run 
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 book 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 

```
package library;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Book_table")
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long bookId;
    private String bookStatus;
    private Long memberId;
    private Long rendtalId;

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
    public Long getRendtalId() {
        return rendtalId;
    }

    public void setRendtalId(Long rendtalId) {
        this.rendtalId = rendtalId;
    }
}


```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package library;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepository extends PagingAndSortingRepository<Book, Long>{
}
```

- 적용 후 REST API 의 테스트 시나리오

1. 예약 -> 예약 내역 확인 -> 결제 확인 -> 예약 처리 확인 -> 예약 취소 -> 결제 취소 확인 -> 예약 취소 처리 확인 -> 마이 페이지
2. 예약 -> 예약 내역 확인 -> 반납 -> 반납 상태 확인 -> 마이 페이지

```
# 사용자가 도서를 예약한다
http POST http://localhost:8081/rentals memberId=1 bookId=1
```

 ![image](https://user-images.githubusercontent.com/66100487/137665939-8e9f8efe-065d-4ab9-b942-cec1ce28708f.png)



```
#Rental 내역 확인
http GET http://localhost:8081/rentals
```

![image](https://user-images.githubusercontent.com/66100487/137669234-5cb7aede-68fc-42be-b8df-85086171ea27.png)


```
# 사용자 예약 후 결제확인
http GET http://localhost:8082/payments
```

![image](https://user-images.githubusercontent.com/66100487/137668046-b97abd6a-20e8-433b-b2a8-0faded7f4688.png)


```
# 사용자 예약한 책 상태 확인
http GET http://localhost:8084/books
```

![image](https://user-images.githubusercontent.com/66100487/137667677-e82a2e5c-257b-49ff-a7f6-7f0828b9017b.png)

```
# 사용자 도서 예약취소
http PATCH http://localhost:8081/rentals/1 reqState="cancel" 
```

![image](https://user-images.githubusercontent.com/66100487/137666138-e53fcac0-d4ee-43f4-bbf0-9159b49468a5.png)


```
# 결제취소 확인
http GET http://localhost:8081/rentals/1
```

![image](https://user-images.githubusercontent.com/66100487/137666495-02e39eab-d9f6-4ef2-b2db-9097b240d675.png)



```
# 사용자 예약 취소한 책 상태 확인
http GET http://localhost:8084/books
```

![image](https://user-images.githubusercontent.com/66100487/137667686-14cd6a5b-cdd5-4d6f-925d-d92cfd3e985e.png)

```
#마이페이지 확인
http GET http://localhost:8083/mypages/1
```

![image](https://user-images.githubusercontent.com/66100487/137667569-d19e1d26-31dc-4594-88df-98f4bcfa7a46.png)

```
# 사용자 도서 예약
http POST http://localhost:8081/rentals memberId=1 bookId=1 
```

![image](https://user-images.githubusercontent.com/66100487/137666784-862cc377-159c-423e-91cf-6961ba44df4b.png)

```
# 사용자 도서 대여
http PATCH http://localhost:8081/rentals/2 reqState="rental" 
```

![image](https://user-images.githubusercontent.com/66100487/137666880-892102cf-9dfb-4119-bd0b-b041cbbc865f.png)


```
# 사용자 대여한 책 상태 확인
http GET http://localhost:8084/books/
```

![image](https://user-images.githubusercontent.com/53402465/105122640-efc66a00-5b19-11eb-9ca2-3671c7b3af80.png)
다시첨부

```
# 사용자 도서 반납
http PATCH http://localhost:8081/rentals/2 reqState="return" 
```

![image](https://user-images.githubusercontent.com/66100487/137666948-695dab55-0392-4a84-908a-8d5f4c7a811c.png)

```
# 사용자 반납한 책 상태 확인
http GET http://localhost:8084/books
```

![image](https://user-images.githubusercontent.com/53402465/105122635-edfca680-5b19-11eb-81c2-5a3b8876ede8.png)
다시첨부

```
#마이페이지 확인
http GET http://localhost:8083/mypages/2
```

![image](https://user-images.githubusercontent.com/66100487/137667577-a3217712-c834-4245-9c44-9d60a8466eed.png)


## 동기식 호출 과 Fallback 처리

분석단계에서의 조건 중 하나로 대여(rental)->결제(payment) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```

@FeignClient(name="payment", url="${api.payment.url}")
public interface PaymentService {

    @RequestMapping(method= RequestMethod.POST, path="/payments")//, fallback = PaymentServiceFallback.class)
    public void payship(@RequestBody Payment payment);

}

```

- 예약 이후(@PostPersist) 결제를 요청하도록 처리
```
- 주문을 받은 직후(@PostPersist) 결제를 요청하도록 처리

    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();


        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        library.external.Payment payment = new library.external.Payment();
        // mappings goes here
        payment.setId(this.id);
        payment.setMemberId(this.memberId);
        payment.setBookId(this.bookId);
        payment.setReqState("reserve");

        RentalApplication.applicationContext.getBean(library.external.PaymentService.class)
            .payship(payment);
    }
```
- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인:

```
# 결제 (payment) 서비스를 잠시 내려놓음

#주문처리
http http://localhost:8081/rentals memberId=1 bookId=1  #Fail 
```

![image](https://user-images.githubusercontent.com/53402465/105120797-3e720500-5b16-11eb-8b2f-d51aea5def12.PNG)

```
#결제서비스 재기동
cd payment
mvn spring-boot:run

#주문처리
http http://localhost:8081/rentals memberId=1 bookId=1   #Success
```

![image](https://user-images.githubusercontent.com/53402465/105120799-3f0a9b80-5b16-11eb-883e-51588b5d6804.PNG)


## 비동기식 호출과 Eventual Consistency

도서관리 시스템은 rental/payment 와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, 도서관리시스템이 유지보수로 인해 잠시 내려간 상태라도 주문을 받는데 문제가 없다:
```

# 도서관리 서비스 (book) 를 잠시 내려놓음

#주문처리
http http://localhost:8081/rentals memberId=1 bookId=1  #Success  

#주문상태 확인
```

![image](https://user-images.githubusercontent.com/53402465/105119392-96f3d300-5b13-11eb-99b0-f9a79bdde8b7.PNG)

```
#상점 서비스 기동
cd book
mvn spring-boot:run

#주문상태 확인
http localhost:8080/rentals     # 모든 주문의 상태가 "reserved"으로 확인
```

![image](https://user-images.githubusercontent.com/53402465/105119394-978c6980-5b13-11eb-8159-65886bee3a81.PNG)



##운영
pipeline 구성
![image](https://user-images.githubusercontent.com/66100487/137645837-def58949-15c7-4cb9-bc2a-dacda91fe014.png)

![image](https://user-images.githubusercontent.com/66100487/137646200-fea5d33b-8934-46a8-ae72-47dfe118a931.png)


