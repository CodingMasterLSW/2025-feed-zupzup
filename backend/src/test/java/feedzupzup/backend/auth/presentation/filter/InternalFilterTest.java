package feedzupzup.backend.auth.presentation.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class InternalFilterTest {

    private static final String SECRET_KEY = "test-internal-secret-key";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private InternalFilter internalFilter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(internalFilter, "secretKey", SECRET_KEY);
    }

    @Test
    @DisplayName("올바른 internal key로 internal API 요청 시 필터를 통과한다")
    void valid_internal_key_passes() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/internal/sse/disconnect");
        given(request.getHeader("X-Internal-Key")).willReturn(SECRET_KEY);

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("internal key가 없으면 403을 반환한다")
    void missing_internal_key_returns_403() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/internal/sse/disconnect");
        given(request.getHeader("X-Internal-Key")).willReturn(null);

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(response).sendError(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isEqualTo(403);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("잘못된 internal key로 요청하면 403을 반환한다")
    void invalid_internal_key_returns_403() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/internal/sse/disconnect");
        given(request.getHeader("X-Internal-Key")).willReturn("wrong-key");

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        ArgumentCaptor<Integer> statusCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(response).sendError(statusCaptor.capture());
        assertThat(statusCaptor.getValue()).isEqualTo(403);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("internal 경로가 아닌 요청은 헤더와 관계없이 필터를 통과한다")
    void non_internal_path_passes() throws Exception {
        // Given
        given(request.getRequestURI()).willReturn("/admin/organizations");

        // When
        internalFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }
}
