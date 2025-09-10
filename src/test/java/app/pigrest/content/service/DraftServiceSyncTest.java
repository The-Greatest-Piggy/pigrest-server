package app.pigrest.content.service;

import app.pigrest.content.domain.DraftRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DraftServiceSyncTest {
    @Mock
    private DraftRepository draftRepository;

    @Mock
    private DraftRedisService draftRedisService;

    @InjectMocks
    private DraftService draftService;

    @Test
    @DisplayName("최신 업데이트된 Draft가 없는 경우")
    void syncCacheToDatabase_noKeys() {

    }

    @Test
    @DisplayName("최신 업데이트된 Draft가 있고, DB에도 데이터가 존재해서, 스케줄러가 동기화하는 경우")
    void syncCacheToDatabase_success() {

    }

    @Test
    @DisplayName("최신 업데이트된 Draft가 있는데, DB에 데이터가 존재하지 않는 경우")
    void syncCacheToDatabase_draftNotFoundInDb() {

    }

    @Test
    @DisplayName("최신 업데이트된 Draft가 있는데, Redis에 Draft 데이터가 없거나 유효하지 않은 경우")
    void syncCacheToDatabase_draftNotFoundInRedis() {

    }
}
