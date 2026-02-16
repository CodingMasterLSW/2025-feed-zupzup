package feedzupzup.backend.guest.application;

import feedzupzup.backend.global.annotation.NonTransactionalRead;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.guest.domain.like.LikeHistory;
import feedzupzup.backend.guest.domain.like.LikeHistoryRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestService {

    private final GuestRepository guestRepository;
    private final WriteHistoryRepository writeHistoryRepository;
    private final LikeHistoryRepository likeHistoryRepository;
    private final GuestActiveTracker guestActiveTracker;

    @Transactional
    public void save(final UUID guestUuid) {
        final Guest guest = new Guest(guestUuid, CurrentDateTime.create());
        guestRepository.save(guest);
    }

    @NonTransactionalRead
    public MyFeedbackListResponse getMyFeedbackPage(
            final UUID organizationUuid,
            final GuestInfo guestInfo
    ) {
        final List<WriteHistory> writeHistories = writeHistoryRepository.findWriteHistoriesBy(
                guestInfo.guestUuid(), organizationUuid);
        return MyFeedbackListResponse.fromHistory(writeHistories);
    }

    @NonTransactionalRead
    public LikeHistoryResponse findGuestLikeHistories(
            final UUID organizationUuid,
            final GuestInfo guestInfo
    ) {
        final List<LikeHistory> likeHistories = likeHistoryRepository.findLikeHistoriesBy(
                guestInfo.guestUuid(), organizationUuid);
        return LikeHistoryResponse.from(likeHistories);
    }

    @NonTransactionalRead
    public boolean isSavedGuest(final UUID guestUuid) {
        return guestRepository.existsByGuestUuid(guestUuid);
    }

    @Transactional
    public boolean updateLastConnectedTime() {
        final List<UUID> sortedActiveGuests = guestActiveTracker.getTodayActiveGuests()
                .stream()
                .sorted()
                .toList();

        if (sortedActiveGuests.isEmpty()) {
            log.info("금일 접속 사용자 수가 없습니다.");
            return true;
        }

        final int updateGuestsCount = guestRepository.updateConnectedTimeForGuests(
                sortedActiveGuests,
                CurrentDateTime.create()
        );

        log.info("금일 접속 사용자 수 : " + updateGuestsCount);
        guestActiveTracker.removeAll(sortedActiveGuests);
        return true;
    }

    @Transactional
    public int removeInactiveGuests() {
        final LocalDateTime targetDateTime = CurrentDateTime.create().minusMonths(3);
        final List<Long> unActivateGuests = guestRepository.findAllByConnectedTimeBefore(
                targetDateTime);
        if (unActivateGuests.isEmpty()) {
            return 0;
        }
        writeHistoryRepository.deleteByGuestIdIn(unActivateGuests);
        likeHistoryRepository.deleteByGuestIdIn(unActivateGuests);
        guestRepository.deleteAllById(unActivateGuests);
        return unActivateGuests.size();

    }
}
