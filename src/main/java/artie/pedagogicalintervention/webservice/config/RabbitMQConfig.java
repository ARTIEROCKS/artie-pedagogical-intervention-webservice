package artie.pedagogicalintervention.webservice.config;

import artie.pedagogicalintervention.webservice.service.ConversationListenerService;
import artie.pedagogicalintervention.webservice.service.TeacherHelpRequestListenerService;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${artie.webservices.conversations.queue}")
    String conversationsQueueName;

    @Value("${artie.webservices.teacherHelpRequests.queue}")
    String teacherHelpRequestsQueueName;

    @Value("${spring.rabbitmq.username}")
    String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Autowired
    private ConversationListenerService conversationListenerService;
    @Autowired
    private TeacherHelpRequestListenerService teacherHelpRequestListenerService;

    @Bean
    Queue conversationsQueue() {
        return new Queue(conversationsQueueName, false);
    }

    @Bean
    Queue teacherHelpRequestsQueue(){return new Queue(teacherHelpRequestsQueueName, false);}

    //create MessageListenerContainer using default connection factory
    @Bean
    MessageListenerContainer conversationListenerContainer(ConnectionFactory connectionFactory ) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.setQueues(conversationsQueue());
        simpleMessageListenerContainer.setMessageListener(conversationListenerService);
        return simpleMessageListenerContainer;
    }

    @Bean
    MessageListenerContainer teacherHelpRequestListenerContainer(ConnectionFactory connectionFactory ) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.setQueues(teacherHelpRequestsQueue());
        simpleMessageListenerContainer.setMessageListener(teacherHelpRequestListenerService);
        return simpleMessageListenerContainer;
    }
}
