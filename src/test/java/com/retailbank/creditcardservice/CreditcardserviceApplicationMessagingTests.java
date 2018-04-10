package com.retailbank.creditcardservice;

import com.retailbank.creditcardservice.gateway.CreditCheckRequest;
import com.retailbank.creditcardservice.gateway.CreditCheckResponse.Score;
import com.retailbank.creditcardservice.listener.CreditScoreProducer;
import com.retailbank.creditcardservice.repository.CreditScoreRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = "com.retailbank:creditcheckservice:+:stubs:8080", workOffline = true)
public class CreditcardserviceApplicationMessagingTests {

    @Autowired
    private StubTrigger stubTrigger;

    @Autowired
    private MessageVerifier messageVerifier;

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    @Autowired
    private CreditScoreProducer creditScoreProducer;

    @Test
    public void shouldStoreResultsOfACreditCheck() {
        // Given
        final UUID uuid = UUID.fromString("e2a8b899-6b62-4010-81f1-9faed24fed2b");

        // When
        stubTrigger.trigger("score_of_high");

        // Then
        final Score score = creditScoreRepository.getScore(uuid);

        assertThat(score).isEqualTo(Score.HIGH);
    }

    @Test
    public void shouldRequestReply() throws InterruptedException {
        creditScoreProducer.requestScore(new CreditCheckRequest(1234));


        stubTrigger.trigger();

        final Object credit_score_requests = messageVerifier.receive("credit_score_requests");

        System.out.println(credit_score_requests);

        Thread.sleep(5000);
    }

}
