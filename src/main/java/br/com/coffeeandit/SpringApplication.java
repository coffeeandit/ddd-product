package br.com.coffeeandit;

import br.com.coffeeandit.config.DomainBusinessException;
import br.com.coffeeandit.config.InfraestructureException;
import br.com.coffeeandit.warehouse.business.WarehouseInOutRecovery;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("br.com.coffeeandit")
@EnableJpaRepositories
@EnableKafka
@EnableRetry
@EnableAsync
public class SpringApplication {

    private Logger LOG = LoggerFactory.getLogger(SpringApplication.class);


    public static void main(String[] args) {
        final long currentTimeMillis = System.currentTimeMillis();
        System.setProperty("spring.kafka.producer.client-id", "product-client" + currentTimeMillis);
        System.setProperty("spring.kafka.producer.producer-id", "product-producer" + currentTimeMillis);



        org.springframework.boot.SpringApplication.run(SpringApplication.class, args);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            final ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            final ConsumerFactory<Object, Object> kafkaConsumerFactory,
            final WarehouseInOutRecovery warehouseInOutRecovery) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);

        var retryTemplate = new RetryTemplate();
        var exceptionClassifierRetryPolicy = new ExceptionClassifierRetryPolicy();
        var classRetryPolicyMap = new HashMap<Class<? extends Throwable>, RetryPolicy>();

        classRetryPolicyMap.put(DomainBusinessException.class, new NeverRetryPolicy());
        classRetryPolicyMap.put(InfraestructureException.class, new SimpleRetryPolicy());
        CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
        compositeRetryPolicy.setPolicies(new RetryPolicy[]{new SimpleRetryPolicy(), exceptionClassifierRetryPolicy});

        exceptionClassifierRetryPolicy.setPolicyMap(classRetryPolicyMap);
        retryTemplate.setRetryPolicy(compositeRetryPolicy);

        factory.setRetryTemplate(retryTemplate);

        final RecoveryCallback<Object> recoveryCallBack = context -> {

            var lastThrowable = context.getLastThrowable();
            var record = (ConsumerRecord<String, String>) context.getAttribute("record");
            var data = record.value();

            LOG.info(String.format("Item que deverá chamar a compensação %s por causa de %s", data, lastThrowable.getMessage()));
            warehouseInOutRecovery.recovery(data);
            return data;
        };
        factory.setRecoveryCallback(recoveryCallBack);
        return factory;
    }

    @Bean
    public KafkaTransactionManager kafkaTransactionManager(ProducerFactory producerFactory) {
        KafkaTransactionManager ktm = new KafkaTransactionManager(producerFactory);
        ;
        ktm.setTransactionSynchronization(AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        return ktm;
    }


    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory em) {
        return new JpaTransactionManager(em);
    }

    @Bean(name = "chainedTransactionManager")
    public ChainedTransactionManager chainedTransactionManager(JpaTransactionManager jpaTransactionManager,
                                                               KafkaTransactionManager kafkaTransactionManager) {
        return new ChainedTransactionManager(kafkaTransactionManager, jpaTransactionManager);
    }

}
