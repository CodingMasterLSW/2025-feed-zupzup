package feedzupzup.backend.guest.infrastructure;

import feedzupzup.backend.global.domain.RetryExecutor;
import feedzupzup.backend.guest.application.GuestService;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GuestScheduler {

    private final GuestService guestService;

    @Scheduled(cron = "0 0 1 * * *")
    public void updateDailyActiveGuestConnectedTime() {
        randomDelay(3000);
        log.info("Active 유저 업데이트 스케줄러 작동 시작");
        int maxRetries = 3;
        RetryExecutor.execute(
                guestService::updateLastConnectedTime,
                maxRetries,
                1000,
                "Active 유저 업데이트"
        );
        log.info("Active 유저 업데이트 스케줄러 작동 완료");
    }

    @Scheduled(cron = "0 5 1 * * *")
    public void removeUnActiveGuest() {
        log.info("비활성 유저 삭제 스케줄러 작동 시작");
        final int deletedCount = guestService.removeInactiveGuests();
        log.info("비활성 유저 {}명 삭제 완료", deletedCount);
        log.info("비활성 유저 삭제 스케줄러 작동 완료");

    }

    private void randomDelay(long maxMillis)  {
        long delay = ThreadLocalRandom.current().nextLong(maxMillis);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
