package feedzupzup.backend.global.message;

public interface MessagePublisher {

    void publish(String topic, Object message);
}
