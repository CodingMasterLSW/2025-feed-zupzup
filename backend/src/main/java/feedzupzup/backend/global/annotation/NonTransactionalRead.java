package feedzupzup.backend.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 단일 조회 메서드에 사용하는 어노테이션.
 * 기존 트랜잭션이 있으면 참여하고, 없으면 트랜잭션 없이 실행합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public @interface NonTransactionalRead {
}